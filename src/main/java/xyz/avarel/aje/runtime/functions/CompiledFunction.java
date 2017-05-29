/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
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

package xyz.avarel.aje.runtime.functions;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.ast.ReturnException;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Type;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.scope.Scope;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Every operation results in the same
 * instance, NOTHING.
 */
public class CompiledFunction extends AJEFunction {
    private final List<Parameter> parameters;
    private final Expr expr;
    private final Scope scope;

    public CompiledFunction(List<Parameter> parameters, Expr expr, Scope scope) {
        this.parameters = parameters;
        this.expr = expr;
        this.scope = scope;
    }

    @Override
    public int getArity() {
        return parameters.size();
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "func(" + parameters.stream().map(Object::toString).collect(Collectors.joining(", ")) + ")";
    }

    @Override
    public Obj invoke(List<Obj> args) {
        Scope scope = this.scope.copy();
        for (int i = 0; i < getArity(); i++) {
            Parameter parameter = parameters.get(i);
            Obj obj = parameter.getType().accept(new ExprVisitor(), scope);
            if (obj instanceof Type) {
                Type type = (Type) obj;
                if (i < args.size()) {
                    if (args.get(i).getType().is(type)) {
                        scope.declare(parameter.getName(), args.get(i));
                    } else {
                        return Undefined.VALUE;
                    }
                } else if (parameter.hasDefault()) {
                    scope.declare(parameter.getName(), parameter.getDefault().accept(new ExprVisitor(), scope));
                } else {
                    return Undefined.VALUE;
                }
            } else {
                return Undefined.VALUE;
            }
        }

        try {
            return expr.accept(new ExprVisitor(), scope);
        } catch (ReturnException re) {
            return re.getValue();
        }
    }
}
