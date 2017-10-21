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

public class PatternTester implements PatternVisitor<Boolean, Tuple> {
    private final PatternCase patternCase;
    private final ExprInterpreter interpreter;
    private final Scope<String, Obj> scope;

    private boolean usedValue = false;

    public PatternTester(PatternCase patternCase, ExprInterpreter interpreter, Scope<String, Obj> scope) {
        this.patternCase = patternCase;
        this.interpreter = interpreter;
        this.scope = scope;
    }

    public boolean test(Tuple tuple) {
        if (tuple.size() < patternCase.arity()) {
            return false;
        }

        for (Pattern pattern : patternCase.getPatterns()) {
            boolean result = pattern.accept(this, tuple);

            if (!result) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Boolean visit(VariablePattern pattern, Tuple obj) {
        return true;
    }

    @Override
    public Boolean visit(DefaultPattern pattern, Tuple obj) {
        return true;
    }

    @Override
    public Boolean visit(ValuePattern pattern, Tuple obj) {
        return true;
    }

    @Override
    public Boolean visit(NestedPattern pattern, Tuple context) {
        return true;
    }
}
