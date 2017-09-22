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

import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Tuple;
import xyz.avarel.kaiper.runtime.pattern.*;

import java.util.Map;

public class RuntimePatternBinder implements RuntimePatternVisitor<Boolean, Tuple> {
    private final Map<String, Obj> scope;

    private final RuntimePatternCase patternCase;

    private boolean usedValue = false;

    public RuntimePatternBinder(RuntimePatternCase patternCase, Map<String, Obj> scope) {
        this.patternCase = patternCase;
        this.scope = scope;
    }

    public boolean bindFrom(Tuple tuple) {
        for (RuntimePattern pattern : patternCase.getPatterns()) {
            boolean result = pattern.accept(this, tuple);

            if (!result) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Boolean visit(VariableRuntimePattern pattern, Tuple obj) {
        Obj value;

        if (obj.hasAttr(pattern.getName())) {
            value = obj.getAttr(pattern.getName());
        } else if (!usedValue && obj.hasAttr("value")) {
            value = obj.getAttr("value");
            usedValue = true;
        } else {
            return false;
        }

        if (!pattern.isNullable() && value == Null.VALUE) {
            return false;
        } else {
            scope.put(pattern.getName(), value);
            return true;
        }
    }

    @Override
    public Boolean visit(DefaultRuntimePattern pattern, Tuple obj) {
        boolean result = pattern.getDelegate().accept(this, obj);

        if (!result) {
            scope.put(pattern.getDelegate().getName(), pattern.getDefault());
        }

        return true;
    }

    @Override
    public Boolean visit(TupleRuntimePattern pattern, Tuple obj) {
        Obj value;

        if (obj.hasAttr(pattern.getName())) {
            value = obj.getAttr(pattern.getName());
        }  else if (!usedValue && obj.hasAttr("value")) {
            value = obj.getAttr("value");
            usedValue = true;
        } else {
            return false;
        }

        return pattern.getObj().equals(value);
    }
}
