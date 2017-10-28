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

public class PatternTester implements PatternVisitor<Boolean, PatternTester.PatternContext> {
    private final ExprInterpreter interpreter;
    private final Scope<String, Obj> scope;

    public PatternTester(ExprInterpreter interpreter, Scope<String, Obj> scope) {
        this.interpreter = interpreter;
        this.scope = scope;
    }

    public boolean test(PatternCase patternCase, Obj obj) {
        PatternContext context = new PatternContext(obj);
        for (Pattern pattern : patternCase.getPatterns()) {
            if (!pattern.accept(this, context)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Boolean visit(VariablePattern pattern, PatternContext context) {
        return context.tuple.size() > context.tupleIndex;
    }

    @Override
    public Boolean visit(DefaultPattern pattern, PatternContext context) {
        return true;
    }

    @Override
    public Boolean visit(RestPattern pattern, PatternContext context) {
        return true;
    }

    @Override
    public Boolean visit(ValuePattern pattern, PatternContext context) {
        if (context.tuple.size() <= context.tupleIndex) {
            return false;
        }

        Obj obj = interpreter.resultOf(pattern.getExpr(), scope);
        return obj.equals(context.tuple.get(context.tupleIndex++));
    }

    @Override
    public Boolean visit(WildcardPattern wildcardPattern, PatternContext context) {
        return context.tuple.size() > context.tupleIndex++;
    }

    @Override
    public Boolean visit(NestedPattern pattern, PatternContext context) {
        if (context.tuple.size() <= context.tupleIndex) {
            return false;
        }

        Obj obj = context.tuple.get(context.tupleIndex++);

        if (!(obj instanceof Tuple)) return false;

        Tuple tuple = (Tuple) obj;

        return test(pattern.getPattern(), tuple);
    }

    static class PatternContext {
        private final Obj tuple;
        private int tupleIndex;

        private PatternContext(Obj tuple) {
            this.tuple = tuple;
        }
    }
}
