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

package xyz.avarel.kaiper.vm.runtime.functions;

import xyz.avarel.kaiper.exceptions.ReturnException;
import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Tuple;
import xyz.avarel.kaiper.runtime.collections.Array;
import xyz.avarel.kaiper.runtime.functions.Func;
import xyz.avarel.kaiper.runtime.functions.Parameter;
import xyz.avarel.kaiper.scope.Scope;
import xyz.avarel.kaiper.vm.compiled.CompiledExecution;
import xyz.avarel.kaiper.vm.compiled.PreparedPatternExecution;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public class CompiledFunc extends Func {
    private final PreparedPatternExecution patternExecutor;
    private final CompiledExecution executor;
    private final Scope scope;

    public CompiledFunc(String name, PreparedPatternExecution patternExecutor, CompiledExecution executor, Scope scope) {
        super(name);
        this.patternExecutor = patternExecutor;
        this.executor = executor;
        this.scope = scope;
    }

    @Override
    public int getArity() {
        if (parameters.isEmpty()) return 0;
        return parameters.get(parameters.size() - 1).isRest() ? parameters.size() - 1 : parameters.size();
    }

    @Override
    public Obj invoke(Tuple argument) {
        try {
            Scope scope = this.scope.subPool();
            for (int i = 0; i < getArity(); i++) {
                CompiledParameter parameter = parameters.get(i);

                if (i < argument.size()) {
                    scope.declare(parameter.getName(), argument.get(i));
                } else if (parameter.hasDefault()) {
                    scope.declare(parameter.getName(), parameter.getDefaultValue().executeWithScope(scope));
                } else {
                    scope.declare(parameter.getName(), Null.VALUE);
                }
            }

            if (!parameters.isEmpty()) {
                Parameter lastParam = parameters.get(parameters.size() - 1);

                if (lastParam.isRest()) {
                    if (argument.size() > getArity()) {
                        List<Obj> sublist = argument.subList(parameters.size() - 1, argument.size());
                        scope.declare(lastParam.getName(), Array.ofList(sublist));
                    } else {
                        scope.declare(lastParam.getName(), new Array());
                    }
                }
            }

            try {
                return executor.executeWithScope(scope);
            } catch (ReturnException re) {
                return re.getValue();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e); //this shouldn't happen but okay so
        }
    }

    @Override
    public List<CompiledParameter> getParameters() {
        return parameters;
    }
}
