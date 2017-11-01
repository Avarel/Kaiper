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

package xyz.avarel.kaiper.runtime.functions;

import xyz.avarel.kaiper.ast.expr.Expr;
import xyz.avarel.kaiper.ast.pattern.PatternBinder;
import xyz.avarel.kaiper.ast.pattern.PatternCase;
import xyz.avarel.kaiper.exceptions.InterpreterException;
import xyz.avarel.kaiper.interpreter.ExprInterpreter;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.Scope;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CompiledFunction extends Function {
    private final Map<PatternCase, Expr> dispatches;

    private final Scope<String, Obj> scope;
    private final ExprInterpreter visitor;

    public CompiledFunction(String name, ExprInterpreter visitor, Scope<String, Obj> scope) {
        this(name, visitor, scope, new TreeMap<>());
    }

    public CompiledFunction(String name, ExprInterpreter visitor, Scope<String, Obj> scope, Map<PatternCase, Expr> dispatches) {
        super(name);
        this.visitor = visitor;
        this.scope = scope;
        this.dispatches = dispatches;
    }

    public boolean addDispatch(PatternCase patternCase, Expr expr) {
        return dispatches.put(patternCase, expr) == null;
    }

    @Override
    public Obj invoke(List<Obj> arguments) {
        for (Map.Entry<PatternCase, Expr> entry : dispatches.entrySet()) {
            Scope<String, Obj> subScope = scope.subScope();

            if (new PatternBinder(visitor, subScope).bind(entry.getKey(), arguments)) {
                return entry.getValue().accept(visitor, subScope);
            }
        }

        throw new InterpreterException("No method matches for " + arguments);
    }
}
