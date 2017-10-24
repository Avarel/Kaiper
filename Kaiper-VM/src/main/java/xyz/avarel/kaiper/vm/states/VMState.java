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

package xyz.avarel.kaiper.vm.states;

import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.Scope;
import xyz.avarel.kaiper.vm.executor.StackMachine;

public class VMState {
    public static void cleanState(StackMachine stackMachine) {
        StatelessStackMachines.reset(stackMachine);
    }

    public static VMState save(StackMachine stackMachine) {
        return new VMState(stackMachine);
    }

    public static VMState saveAndCleanState(StackMachine stackMachine) {
        VMState state = save(stackMachine);
        cleanState(stackMachine);
        return state;
    }

    private final int stackLock;
    private final Scope<String, Obj> scope;
    private final long lineNumber;
    private final String[] stringPool;

    private VMState() {
        stackLock = 0;
        scope = null;
        lineNumber = -1;
        stringPool = null;
    }

    private VMState(StackMachine stackMachine) {
        stackLock = stackMachine.stack.getLock();
        scope = stackMachine.scope;
        lineNumber = stackMachine.lineNumber;
        stringPool = stackMachine.stringPool;
    }

    public VMState saveAndLoadState(StackMachine stackMachine) {
        VMState state = save(stackMachine);
        load(stackMachine);
        return state;
    }


    public void load(StackMachine stackMachine) {
        stackMachine.stack.setLock(stackLock);
        stackMachine.scope = scope;
        stackMachine.lineNumber = lineNumber;
    }
}
