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
import xyz.avarel.kaiper.runtime.IndexedObj;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Tuple;
import xyz.avarel.kaiper.runtime.collections.Array;
import xyz.avarel.kaiper.scope.Scope;

public class PatternBinder implements PatternVisitor<Boolean, PatternBinder.PatternContext> {
    private final ExprInterpreter interpreter;
    private final Scope<String, Obj> scope;

    public PatternBinder(ExprInterpreter interpreter, Scope<String, Obj> scope) {
        this.interpreter = interpreter;
        this.scope = scope;
    }

    public boolean bind(PatternCase patternCase, IndexedObj obj) {
        PatternContext context = new PatternContext(patternCase, obj);
        for (Pattern pattern : patternCase.getPatterns()) {
            if (!pattern.accept(this, context)) {
                return false;
            }
            context.currentPatternIndex++;
        }

        return true;
    }

    @Override
    public Boolean visit(VariablePattern pattern, PatternContext context) {
        if (context.tuple.size() <= context.tupleIndex) {
            return false;
        }

        ExprInterpreter.declare(scope, pattern.getName(), context.tuple.get(context.tupleIndex++));
        return true;
    }

    @Override
    public Boolean visit(DefaultPattern pattern, PatternContext context) {
        // def what(x = 1, y = 2, z) = "$x $y $z"
        //def what(a, b=2, c=3,d=4,e) = [a,b,c,d,e]
        // def what(a, b=2, c,d=4,e) = [a,b,c,d,e]

        int remainingRequiredArgs = context.patternCase.subList(context.currentPatternIndex).arity();
        int remainingTupleElements = context.tuple.size() - context.tupleIndex;
        if (remainingRequiredArgs >= remainingTupleElements || !pattern.getDelegate().accept(this, context)) {
            ExprInterpreter.declare(scope, pattern.getName(), pattern.getDefault().accept(interpreter, scope));
        }

        return true;
    }

    @Override
    public Boolean visit(RestPattern pattern, PatternContext context) {
        int remainingRequiredArgs = context.patternCase.subList(context.currentPatternIndex).arity();
        int remainingTupleElements = context.tuple.size() - context.tupleIndex;

        Array array = new Array();

        for (; remainingTupleElements > remainingRequiredArgs; remainingTupleElements--) {
            array.add(context.tuple.get(context.tupleIndex++));
        }

        ExprInterpreter.declare(scope, pattern.getName(), array);

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
    public Boolean visit(TuplePattern pattern, PatternContext context) {
        if (context.tuple.size() <= context.tupleIndex) {
            return false;
        }

        Obj obj = context.tuple.get(context.tupleIndex++);

        if (obj instanceof Tuple) {
            return bind(pattern.getPattern(), (IndexedObj) obj);
        } else {
            return false;
        }
    }

    static class PatternContext {
        private final PatternCase patternCase;
        private final IndexedObj tuple;

        private int currentPatternIndex;
        private int tupleIndex;

        private PatternContext(PatternCase patternCase, IndexedObj tuple) {
            this.patternCase = patternCase;
            this.tuple = tuple;
        }
    }
}
