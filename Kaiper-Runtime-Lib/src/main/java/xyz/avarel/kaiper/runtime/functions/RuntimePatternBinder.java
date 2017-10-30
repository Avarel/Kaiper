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

import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.collections.Array;
import xyz.avarel.kaiper.runtime.runtime_pattern.*;

import java.util.Map;

public class RuntimePatternBinder implements PatternVisitor<Boolean, RuntimePatternBinder.PatternContext> {
    private final Map<String, Obj> scope;

    public RuntimePatternBinder(Map<String, Obj> scope) {
        this.scope = scope;
    }

    public boolean bind(PatternCase patternCase, Obj obj) {
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

        scope.put(pattern.getName(), context.tuple.get(context.tupleIndex++));
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
            scope.put(pattern.getName(), pattern.getDefault().apply(scope));
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

        scope.put(pattern.getName(), array);

        return true;
    }

    @Override
    public Boolean visit(NestedPattern pattern, PatternContext context) {
        if (context.tuple.size() <= context.tupleIndex) {
            return false;
        }

        Obj obj = context.tuple.get(context.tupleIndex++);
        return bind(pattern.getPattern(), obj);
    }

    static class PatternContext {
        private final PatternCase patternCase;
        private final Obj tuple;

        private int currentPatternIndex;
        private int tupleIndex;

        private PatternContext(PatternCase patternCase, Obj tuple) {
            this.patternCase = patternCase;
            this.tuple = tuple;
        }
    }
}
