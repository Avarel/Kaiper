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

import xyz.avarel.kaiper.vm.executor.StackMachine;

import java.util.Stack;
import java.util.Vector;

public class StatelessStackMachines {
    private static final Stack<StackMachine> freePool = new Stack<>();
    private static final Vector<StackMachine> idPool = new Vector<>();

    public static StackMachine borrow() {
        StackMachine machine;
        synchronized (freePool) {
            if (!freePool.empty()) {
                machine = freePool.pop();
            } else {
                machine = new StackMachine();
                idPool.add(machine);
                return machine;
            }
        }

        machine.resetTimeout();

        return machine;
    }

    public static void freeUnusedResources() {
        synchronized (freePool) {
            idPool.removeAll(freePool);
            freePool.clear();
        }
    }

    public static void reset(StackMachine machine) {
        machine.scope = null;
        machine.stack.unlock();
        machine.stack.clear();
        machine.lineNumber = -1;
        machine.byteBuffer.reset();
        machine.stringPool = null;
        machine.patternProcessor.current = 0;
        machine.patternProcessor.position = 0;
        machine.patternProcessor.size = 0;
        machine.patternProcessor.obj = null;
        machine.patternProcessor.results = null;
        machine.breakpointMillis = -1;
    }

    public static void release(StackMachine machine) {
        if (!idPool.contains(machine))
            throw new IllegalArgumentException("StackMachine " + machine + " wasn't created by this class.");

        reset(machine);

        synchronized (freePool) {
            if (!freePool.contains(machine)) {
                freePool.push(machine);
            }
        }

    }
}
