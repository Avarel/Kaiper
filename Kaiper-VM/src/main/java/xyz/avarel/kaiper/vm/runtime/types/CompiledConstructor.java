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

package xyz.avarel.kaiper.vm.runtime.types;

import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Tuple;
import xyz.avarel.kaiper.runtime.collections.Array;
import xyz.avarel.kaiper.runtime.functions.Parameter;
import xyz.avarel.kaiper.runtime.types.Constructor;
import xyz.avarel.kaiper.vm.compiled.CompiledExecution;
import xyz.avarel.kaiper.vm.compiled.PreparedPatternExecution;
import xyz.avarel.kaiper.vm.patterns.PatternCase;
import xyz.avarel.kaiper.vm.runtime.functions.CompiledParameter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public class CompiledConstructor extends Constructor {
    private final PatternCase patternCase;
    private final Scope scope;
    private final CompiledExecution executor;

    public CompiledConstructor(PreparedPatternExecution patternCase, CompiledExecution executor, Scope scope) {
        this.patternCase = patternCase;
        this.executor = executor;
        this.scope = scope;
    }

    @Override
    public CompiledObj invoke(Tuple argument) {
        return eval(argument, this.scope);
    }

    public List<CompiledParameter> getPatternCase() {
        return patternCase;
    }

    private CompiledObj eval(List<Obj> arguments, Scope scope) {
        try {
            if (!(targetType instanceof CompiledType)) {
                throw new ComputeException("Internal error");
            }

            Scope constructorScope = scope.subPool();

            for (int i = 0; i < getArity(); i++) {
                CompiledParameter parameter = patternCase.get(i);

                if (i < arguments.size()) {
                    constructorScope.declare(parameter.getName(), arguments.get(i));
                } else if (parameter.hasDefault()) {
                    constructorScope.declare(parameter.getName(), parameter.getDefaultValue().executeWithScope(scope));
                } else {
                    constructorScope.declare(parameter.getName(), Null.VALUE);
                }
            }


            if (!patternCase.isEmpty()) {
                Parameter lastParam = patternCase.get(patternCase.size() - 1);

                if (lastParam.isRest()) {
                    if (arguments.size() > getArity()) {
                        List<Obj> sublist = arguments.subList(patternCase.size() - 1, arguments.size());
                        constructorScope.declare(lastParam.getName(), Array.ofList(sublist));
                    } else {
                        constructorScope.declare(lastParam.getName(), new Array());
                    }
                }
            }

            CompiledObj parent = null;

            if (targetType.hasParent()) {
                List<Obj> superArguments = new ArrayList<>(superParameters);

                if (targetType.getParent() != Obj.TYPE) {
                    if (!(targetType.getParent() instanceof CompiledType)) {
                        throw new ComputeException(targetType.getParent() + " can not be extended");
                    }

                    CompiledConstructor parentConstructor = ((CompiledType) targetType.getParent()).getConstructor();

                    parent = parentConstructor.invoke(superArguments);
                    scope = scope.combine(parent.getScope());

                    constructorScope.declare("super", parent);
                }
            }

            CompiledObj instance = new CompiledObj((CompiledType) targetType, parent, scope.subPool());

            constructorScope.declare("this", instance);

            executor.executeWithScope(constructorScope);

            return instance;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}