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

import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.Scope;
import xyz.avarel.kaiper.vm.executor.StackMachine;

public class CompiledScopedExecution extends CompiledExecution {
    private final Scope<String, Obj> baseScope;

    public CompiledScopedExecution(byte[] bytecode, String[] stringPool, Scope<String, Obj> baseScope) {
        super(bytecode, stringPool);
        this.baseScope = baseScope;
    }

    public Obj execute() {
        return execute(baseScope.subScope());
    }

    public Obj execute(StackMachine executor) {
        return execute(executor, baseScope.subScope());
    }
}
