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
import xyz.avarel.kaiper.runtime.Tuple;
import xyz.avarel.kaiper.vm.executor.StackMachine;
import xyz.avarel.kaiper.vm.states.StatelessStackMachines;
import xyz.avarel.kaiper.vm.states.VMState;

import java.io.ByteArrayInputStream;

public class PreparedPatternExecution {
    private final int patternArity;
    private final byte[] bytecode;
    private final String[] stringPool;

    public PreparedPatternExecution(int patternArity, byte[] bytecode, String[] stringPool) {
        this.patternArity = patternArity;
        this.bytecode = bytecode;
        this.stringPool = stringPool;
    }


    public boolean executeAssign(Scope scope, Tuple tuple) {
        StackMachine machine = StatelessStackMachines.borrow();
        machine.stringPool = stringPool;
        machine.scope = scope;

        try {
            return machine.patternProcessor.assignFrom(tuple, patternArity, new KDataInputStream(new ByteArrayInputStream(bytecode)));
        } finally {
            StatelessStackMachines.release(machine);
        }
    }

    public boolean executeDeclare(Scope scope, Tuple tuple) {
        StackMachine machine = StatelessStackMachines.borrow();
        machine.stringPool = stringPool;
        machine.scope = scope;

        try {
            return machine.patternProcessor.declareFrom(tuple, patternArity, new KDataInputStream(new ByteArrayInputStream(bytecode)));
        } finally {
            StatelessStackMachines.release(machine);
        }
    }

    public boolean executeAssign(StackMachine machine, Scope scope, Tuple tuple) {
        if (machine == null) {
            return executeAssign(scope, tuple);
        }

        VMState state = VMState.save(machine);

        //load temp state
        machine.scope = scope;
        machine.stringPool = stringPool;

        try {
            return machine.patternProcessor.assignFrom(tuple, patternArity, new KDataInputStream(new ByteArrayInputStream(bytecode)));
        } finally {
            state.load(machine);
        }
    }

    public boolean executeDeclare(StackMachine machine, Scope scope, Tuple tuple) {
        if (machine == null) {
            return executeAssign(scope, tuple);
        }

        VMState state = VMState.save(machine);

        //load temp state
        machine.scope = scope;
        machine.stringPool = stringPool;

        try {
            return machine.patternProcessor.declareFrom(tuple, patternArity, new KDataInputStream(new ByteArrayInputStream(bytecode)));
        } finally {
            state.load(machine);
        }
    }

    public int getPatternArity() {
        return patternArity;
    }
}
