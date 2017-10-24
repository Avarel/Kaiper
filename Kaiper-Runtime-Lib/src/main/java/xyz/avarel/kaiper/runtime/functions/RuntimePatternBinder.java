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
import xyz.avarel.kaiper.runtime.pattern.*;

import java.util.Map;

public class RuntimePatternBinder implements RuntimePatternVisitor<Boolean, RTPatternContext> {
    private final RuntimePatternCase patternCase;

    public RuntimePatternBinder(RuntimePatternCase patternCase) {
        this.patternCase = patternCase;
    }

    public boolean bindFrom(Map<String, Obj> scope, Obj tuple) {
        RTPatternContext context = new RTPatternContext(scope, tuple, 0);
        for (RTPattern pattern : patternCase.getPatterns()) {
            boolean result = pattern.accept(this, context);

            if (!result) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Boolean visit(VariableRTPattern pattern, RTPatternContext patternContext) {
        if (patternContext.tuple.size() <= patternContext.position) {
            return false;
        }

        patternContext.scope.put(pattern.getName(), patternContext.tuple.get(patternContext.position++));
        return true;
    }

    @Override
    public Boolean visit(DefaultRTPattern pattern, RTPatternContext patternContext) {
        if (!pattern.getDelegate().accept(this, patternContext)) {
            patternContext.scope.put(pattern.getName(), pattern.getDefault());
        }

        return true;
    }

    @Override
    public Boolean visit(ValueRTPattern pattern, RTPatternContext patternContext) {
        Obj obj = pattern.getObj();

        return obj.equals(patternContext.tuple.get(patternContext.position++));
    }
}
