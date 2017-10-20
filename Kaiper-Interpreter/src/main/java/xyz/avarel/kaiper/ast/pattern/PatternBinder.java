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
    private final PatternCase patternCase;

    public PatternBinder(PatternCase patternCase) {
        this.patternCase = patternCase;
    }

    public boolean declareFrom(ExprInterpreter interpreter, Scope<String, Obj> scope, Tuple tuple) {
        PatternContext context = new PatternContext(interpreter, scope, tuple, 0);
        for (Pattern pattern : patternCase.getPatterns()) {
            boolean result = pattern.accept(this, context);

            if (!result) {
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

        ExprInterpreter.declare(patternContext.scope, pattern.getName(), patternContext.tuple.get(patternContext.position++));
        return true;
    }

    @Override
    public Boolean visit(DefaultPattern pattern, PatternContext patternContext) {
        if (!pattern.getDelegate().accept(this, patternContext)) {
            ExprInterpreter.declare(patternContext.scope, pattern.getName(), pattern.getDefault().accept(patternContext.interpreter, patternContext.scope));
        }

        return true;
    }

    @Override
    public Boolean visit(ValuePattern pattern, PatternContext patternContext) {
        Obj obj = patternContext.interpreter.resultOf(pattern.getExpr(), patternContext.scope);

        return obj.equals(patternContext.tuple.get(patternContext.position++));
    }

    @Override
    public Boolean visit(NestedPattern pattern, PatternContext patternContext) {
        return null;
    }
}
