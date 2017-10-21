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

package xyz.avarel.kaiper.ast.pattern;

import xyz.avarel.kaiper.interpreter.ExprInterpreter;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Tuple;
import xyz.avarel.kaiper.scope.Scope;

public class PatternBinder implements PatternVisitor<Boolean, PatternContext> {
    private final ExprInterpreter interpreter;
    private final Scope<String, Obj> scope;

    public PatternBinder(ExprInterpreter interpreter, Scope<String, Obj> scope) {
        this.interpreter = interpreter;
        this.scope = scope;
    }

    public boolean declareFrom(PatternCase patternCase, Tuple tuple) {
        System.out.println(patternCase);

        PatternContext context = new PatternContext(patternCase, tuple, 0);
        for (Pattern pattern : patternCase.getPatterns()) {
            if (!pattern.accept(this, context)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Boolean visit(VariablePattern pattern, PatternContext patternContext) {
        if (patternContext.tuple.size() <= patternContext.position) {
            return false;
        }

        ExprInterpreter.declare(scope, pattern.getName(), patternContext.tuple.get(patternContext.position++));
        return true;
    }

    @Override
    public Boolean visit(DefaultPattern pattern, PatternContext patternContext) {
        if (!pattern.getDelegate().accept(this, patternContext)) {
            ExprInterpreter.declare(scope, pattern.getName(), pattern.getDefault().accept(interpreter, scope));
        }

        return true;
    }

    @Override
    public Boolean visit(ValuePattern pattern, PatternContext patternContext) {
        if (patternContext.tuple.size() <= patternContext.position) {
            return false;
        }

        Obj obj = interpreter.resultOf(pattern.getExpr(), scope);
        return obj.equals(patternContext.tuple.get(patternContext.position++));
    }

    @Override
    public Boolean visit(NestedPattern pattern, PatternContext patternContext) {
        return patternContext.tuple.size() > patternContext.position &&
                new PatternBinder(interpreter, scope).declareFrom(
                        pattern.getPattern(),
                        patternContext.tuple.get(patternContext.position++)
                                .as(Tuple.TYPE)
        );
    }
}
