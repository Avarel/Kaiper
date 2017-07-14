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

package xyz.avarel.aje.interpreter.runtime.functions;

import xyz.avarel.aje.ReturnException;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.interpreter.ExprInterpreter;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.collections.Array;
import xyz.avarel.aje.runtime.functions.Func;
import xyz.avarel.aje.runtime.functions.Parameter;
import xyz.avarel.aje.runtime.types.Type;
import xyz.avarel.aje.scope.Scope;

import java.util.List;
import java.util.stream.Collectors;

public final class CompiledFunc extends Func {
    private final String name;
    private final List<Parameter> parameters;
    private final Expr expr;
    private final Scope scope;
    private final ExprInterpreter visitor;

    public CompiledFunc(String name, List<Parameter> parameters, Expr expr, ExprInterpreter visitor, Scope scope) {
        this.name = name;
        this.parameters = parameters;
        this.expr = expr;
        this.scope = scope;
        this.visitor = visitor;
    }

    @Override
    public int getArity() {
        if (parameters.isEmpty()) return 0;
        return parameters.get(parameters.size() - 1).isRest() ? parameters.size() - 1 : parameters.size();
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "func" + (name == null ? "" : " " + name) + "(" + getParameters().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ")) + ")";
    }

    @Override
    public Obj invoke(List<Obj> arguments) {
        Scope scope = this.scope.subPool();
        for (int i = 0; i < getArity(); i++) {
            Parameter parameter = parameters.get(i);

            Type type = parameter.getType();

            if (i < arguments.size()) {
                if (arguments.get(i).getType().is(type)) {
                    scope.declare(parameter.getName(), arguments.get(i));
                } else if (type == Obj.TYPE) {
                    scope.declare(parameter.getName(), Undefined.VALUE);
                } else {
                    throw typeError(parameters, arguments);
                }
            } else if (parameter.hasDefault()) {
                scope.declare(parameter.getName(), parameter.getDefault());
            } else if (type == Obj.TYPE) {
                scope.declare(parameter.getName(), Undefined.VALUE);
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

                    scope.declare(lastParam.getName(), array);
                } else {
                    scope.declare(lastParam.getName(), new Array());
                }
            }
        }

        try {
            return expr.accept(visitor, scope);
        } catch (ReturnException re) {
            return re.getValue();
        }
    }
}
