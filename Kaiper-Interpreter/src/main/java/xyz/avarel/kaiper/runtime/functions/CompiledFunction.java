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
import xyz.avarel.kaiper.ast.expr.tuples.MatchExpr;
import xyz.avarel.kaiper.ast.expr.variables.Identifier;
import xyz.avarel.kaiper.ast.pattern.PatternCase;
import xyz.avarel.kaiper.interpreter.ExprInterpreter;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.Scope;

import java.util.TreeMap;

public class CompiledFunction extends Function {
    private final MatchExpr matchExpr;

    private final Scope<String, Obj> scope;
    private final ExprInterpreter visitor;

    public CompiledFunction(String name, ExprInterpreter visitor, Scope<String, Obj> scope) {
        this(name, visitor, scope, new MatchExpr(null, new Identifier(null, "argument"), new TreeMap<>()));
    }

    public CompiledFunction(String name, ExprInterpreter visitor, Scope<String, Obj> scope, MatchExpr expr) {
        super(name);
        this.visitor = visitor;
        this.scope = scope;
        this.matchExpr = expr;
    }

    public boolean addCase(PatternCase patternCase, Expr expr) {
        return matchExpr.getCases().put(patternCase, expr) == null;
    }

//    @Override
//    public Obj divide(Obj other) {
//       if (other instanceof Int) {
//           return specificArity(((Int) other));
//       }
//       return super.divide(other);
//    }
//
//    public Function specificArity(Int other) {
//        SortedSet<CompiledFunction> b = new TreeSet<>();
//        for (CompiledFunction func : branches) {
//            if (func.getArity() == other.value()) {
//                b.add(func);
//            }
//        }
//        return b.size() == 1 ? b.first() : new CompiledFunction(getName(), b);
//    }

    @Override
    public Obj invoke(Obj argument) {
        Scope<String, Obj> subScope = scope.subScope();

        ExprInterpreter.declare(subScope, "argument", argument);

        return matchExpr.accept(visitor, subScope);
    }
}
