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

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.pattern.PatternBinder;
import xyz.avarel.kaiper.ast.pattern.PatternCase;
import xyz.avarel.kaiper.exceptions.InterpreterException;
import xyz.avarel.kaiper.exceptions.ReturnException;
import xyz.avarel.kaiper.interpreter.ExprInterpreter;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Tuple;
import xyz.avarel.kaiper.scope.Scope;

public class CompiledFunc extends Func implements Comparable<CompiledFunc> {
    private final PatternCase patternCase;
    private final Expr expr;

    private final Scope<String, Obj> scope;
    private final ExprInterpreter visitor;

    public CompiledFunc(String name, PatternCase patternCase, Expr expr, ExprInterpreter visitor, Scope<String, Obj> scope) {
        super(name);
        this.patternCase = patternCase;
        this.expr = expr;
        this.scope = scope;
        this.visitor = visitor;
    }

    public PatternCase getPattern() {
        return patternCase;
    }

    @Override
    public int getArity() {
        return patternCase.size();
    }

    // def fun(x, ...y, z = 5) { println x; println y; println z }
    @Override
    public Obj invoke(Tuple argument) {
        Scope<String, Obj> scope = this.scope.subScope();

        if (!new PatternBinder(patternCase).declareFrom(visitor, scope, argument)) {
            throw new InterpreterException("Could not match arguments (" + argument + ") to " + getName() + "(" + patternCase + ")");
        }

        try {
            return expr.accept(visitor, scope);
        } catch (ReturnException re) {
            return re.getValue();
        }
    }

    @Override
    public String toString() {
        return super.toString() + getPattern() + " { ... }";
    }

    @Override
    public int compareTo(CompiledFunc o) {
        return patternCase.compareTo(o.patternCase);
    }

    public Scope<String, Obj> getScope() {
        return scope;
    }

    public ExprInterpreter getVisitor() {
        return visitor;
    }

    public Expr getExpr() {
        return expr;
    }
}
