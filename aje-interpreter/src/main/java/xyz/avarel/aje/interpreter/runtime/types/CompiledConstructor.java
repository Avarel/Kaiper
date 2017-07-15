/*
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package xyz.avarel.aje.interpreter.runtime.types;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.Single;
import xyz.avarel.aje.exceptions.ComputeException;
import xyz.avarel.aje.interpreter.ExprInterpreter;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.collections.Array;
import xyz.avarel.aje.runtime.functions.Parameter;
import xyz.avarel.aje.runtime.types.Constructor;
import xyz.avarel.aje.runtime.types.Type;
import xyz.avarel.aje.scope.Scope;

import java.util.ArrayList;
import java.util.List;

public class CompiledConstructor extends Constructor {
    private final List<Parameter> parameters;
    private final List<Single> superInvocation;
    private final ExprInterpreter visitor;
    private final Scope scope;
    private final Expr expr;

    public CompiledConstructor(List<Parameter> parameters, List<Single> superInvocation, Expr expr, ExprInterpreter visitor, Scope scope) {
        this.parameters = parameters;
        this.superInvocation = superInvocation;
        this.visitor = visitor;
        this.scope = scope;
        this.expr = expr;
    }

    @Override
    public List<Parameter> getParameters() {
        return parameters;
    }

    @Override
    public CompiledObj invoke(List<Obj> arguments) {
        return eval(arguments, this.scope);
    }

    private CompiledObj eval(List<Obj> arguments, Scope scope) {
        Scope constructorScope = scope.subPool();

        for (int i = 0; i < getArity(); i++) {
            Parameter parameter = parameters.get(i);

            Type type = parameter.getType();

            if (i < arguments.size()) {
                if (arguments.get(i).getType().is(type)) {
                    constructorScope.declare(parameter.getName(), arguments.get(i));
                } else if (type == Obj.TYPE) {
                    constructorScope.declare(parameter.getName(), Undefined.VALUE);
                } else {
                    throw typeError(parameters, arguments);
                }
            } else if (parameter.hasDefault()) {
                constructorScope.declare(parameter.getName(), parameter.getDefault());
            } else if (type == Obj.TYPE) {
                constructorScope.declare(parameter.getName(), Undefined.VALUE);
            } else {
                throw typeError(parameters, arguments);
            }
        }

        if (!parameters.isEmpty()) {
            Parameter lastParam = parameters.get(parameters.size() - 1);

            if (lastParam.isRest()) {
                if (arguments.size() > getArity()) {
                    Type type = lastParam.getType();
                    List<Obj> sublist = arguments.subList(parameters.size() - 1, arguments.size());
                    Array array = new Array();

                    for (Obj obj : sublist) {
                        if (obj.getType().is(type)) {
                            array.add(obj);
                        } else if (type != Obj.TYPE) {
                            throw typeError(parameters, arguments);
                        }
                    }

                    constructorScope.declare(lastParam.getName(), array);
                } else {
                    constructorScope.declare(lastParam.getName(), new Array());
                }
            }
        }

        if (targetType.hasParent()) {
            List<Obj> superArguments = new ArrayList<>(superInvocation.size());

            for (Expr expr : superInvocation) {
                superArguments.add(expr.accept(visitor, constructorScope));
            }

            if (targetType.getParent() != Obj.TYPE) {
                Constructor parentConstructor = targetType.getParent().getConstructor();

                if (parentConstructor == null) {
                    throw new ComputeException(targetType.getParent() + " can not be extended (no constructor)");
                }

                Obj superObject = parentConstructor.invoke(superArguments);
                if (parentConstructor instanceof CompiledConstructor) {
                    scope = scope.combine(((CompiledObj) superObject).getScope());
                }

                constructorScope.declare("super", superObject);
            }
        }


        CompiledObj instance = new CompiledObj(targetType, scope.subPool().withFlags(scope.getFlagsMap()));

        constructorScope.declare("this", instance);

        expr.accept(visitor, constructorScope);

        return instance;
    }
}
