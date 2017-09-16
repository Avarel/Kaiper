/*
 *  Copyright 2017 An Tran and Adrian Todt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package xyz.avarel.kaiper.vm.compiled;

import xyz.avarel.kaiper.bytecode.io.KDataInputStream;
import xyz.avarel.kaiper.bytecode.opcodes.KOpcodes;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.Scope;
import xyz.avarel.kaiper.vm.executor.StackMachine;
import xyz.avarel.kaiper.vm.states.StatelessStackMachines;
import xyz.avarel.kaiper.vm.states.VMState;

import java.io.ByteArrayInputStream;

public class CompiledExecution {
    private final byte[] bytecode;
    private final String[] stringPool;

    public CompiledExecution(byte[] bytecode, String[] stringPool) {
        this.bytecode = bytecode;
        this.stringPool = stringPool;
    }

    public String[] getStringPool() {
        return stringPool;
    }

    public Obj execute(Scope scope) {
        StackMachine machine = StatelessStackMachines.borrow();
        machine.stringPool = stringPool;
        machine.scope = scope;

        KOpcodes.READER.read(machine, new KDataInputStream(new ByteArrayInputStream(bytecode)));
        Obj result = machine.stack.pop();

        StatelessStackMachines.release(machine);

        return result;
    }

    public Obj execute(StackMachine machine, Scope scope) {
        if (machine == null) {
            return execute(scope);
        }

        //load temp state
        VMState state = VMState.save(machine);
        machine.stringPool = stringPool;
        machine.scope = scope;

        KOpcodes.READER.read(machine, new KDataInputStream(new ByteArrayInputStream(bytecode)));
        Obj result = machine.stack.pop();

        state.load(machine);

        return result;
    }
}
