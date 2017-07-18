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

import xyz.avarel.aje.exceptions.ReturnException;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.interpreter.ExprInterpreter;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.collections.Array;
import xyz.avarel.aje.runtime.functions.Func;
import xyz.avarel.aje.scope.Scope;

import java.util.List;

public class CompiledFunc extends Func {
    private final List<CompiledParameter> parameters;
    private final Expr expr;
    private final Scope scope;
    private final ExprInterpreter visitor;

    public CompiledFunc(String name, List<CompiledParameter> parameters, Expr expr, ExprInterpreter visitor, Scope scope) {
        super(name);
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

    public List<CompiledParameter> getParameters() {
        return parameters;
    }

    @Override
    public Obj invoke(List<Obj> arguments) {
        Scope scope = this.scope.subPool();
        for (int i = 0; i < getArity(); i++) {
            CompiledParameter parameter = parameters.get(i);

            if (i < arguments.size()) {
                scope.declare(parameter.getName(), arguments.get(i));
            } else if (parameter.hasDefault()) {
                scope.declare(parameter.getName(), parameter.getDefaultExpr().accept(visitor, scope));
            } else {
                scope.declare(parameter.getName(), Undefined.VALUE);
            }
        }

        if (!parameters.isEmpty()) {
            CompiledParameter lastParam = parameters.get(parameters.size() - 1);

            if (lastParam.isRest()) {
                if (arguments.size() > getArity()) {
                    List<Obj> sublist = arguments.subList(parameters.size() - 1, arguments.size());
                    scope.declare(lastParam.getName(), Array.ofList(sublist));
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
