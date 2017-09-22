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

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class CompiledMultiMethod extends Func {
    private final SortedMap<PatternCase, Expr> methodCases = new TreeMap<>();
    private final ExprInterpreter visitor;
    private final Scope scope;

    public CompiledMultiMethod(String name, ExprInterpreter visitor, Scope scope) {
        super(name);
        this.visitor = visitor;
        this.scope = scope;
    }

    public Map<PatternCase, Expr> getMethodCases() {
        return methodCases;
    }

    public void addCase(PatternCase pattern, Expr expr) {
        methodCases.put(pattern, expr);
    }

    @Override
    public int getArity() {
        return methodCases.firstKey().size();
    }

    @Override
    public Obj invoke(Tuple argument) {
        for (Map.Entry<PatternCase, Expr> entry : methodCases.entrySet()) {
            Scope scope = this.scope.subPool();

            if (!new PatternBinder(entry.getKey(), visitor, scope).declareFrom(argument)) {
                continue;
            }

            try {
                return entry.getValue().accept(visitor, scope);
            } catch (ReturnException re) {
                return re.getValue();
            }
        }

        throw new InterpreterException("Could not match arguments (" + argument + ") to any method cases");
    }

    @Override
    public String toString() {
        return super.toString() + "(" + (methodCases.size() == 1 ? methodCases.firstKey() : methodCases.size() + " definitions") + ")";
    }
}
