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

import xyz.avarel.kaiper.Pair;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Tuple;
import xyz.avarel.kaiper.runtime.pattern.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class RuntimeLibPatternBinder implements RuntimePatternVisitor<Pair<String, Obj>, Tuple> {
    private static final Pair<String, Obj> SUCCESS_NO_ASSIGNMENT = new Pair<>(null, null);

    private final Map<String, Obj> scope;

    private final RuntimeLibPatternCase patternCase;

    private boolean usedValue = false;

    public RuntimeLibPatternBinder(RuntimeLibPatternCase patternCase, Map<String, Obj> scope) {
        this.patternCase = patternCase;
        this.scope = scope;
    }

    public boolean bindFrom(Tuple tuple) {
        Map<String, Obj> results = new LinkedHashMap<>();
        for (RuntimeLibPattern pattern : patternCase.getPatterns()) {
            Pair<String, Obj> result = pattern.accept(this, tuple);

            if (result != null) {
                if (result != SUCCESS_NO_ASSIGNMENT) {
                    results.put(result.getFirst(), result.getSecond());
                }
            } else {
                return false;
            }
        }

        for (Map.Entry<String, Obj> result : results.entrySet()) {
            scope.put(result.getKey(), result.getValue());
        }

        return true;
    }

    @Override
    public Pair<String, Obj> visit(VariableRuntimeLibPattern pattern, Tuple obj) {
        Obj value;

        if (obj.hasAttr(pattern.getName())) {
            value = obj.getAttr(pattern.getName());
        } else if (!usedValue && obj.hasAttr("value")) {
            value = obj.getAttr("value");
            usedValue = true;
        } else {
            return null;
        }

        return new Pair<>(pattern.getName(), value);
    }

    @Override
    public Pair<String, Obj> visit(DefaultRuntimeLibPattern pattern, Tuple obj) {
        Pair<String, Obj> result = pattern.getDelegate().accept(this, obj);

        if (result == null) {
            Obj value = pattern.getDefault();
            return new Pair<>(pattern.getDelegate().getName(), value);
        } else {
            return result;
        }
    }

    @Override
    public Pair<String, Obj> visit(TupleRuntimeLibPattern pattern, Tuple obj) {
        Obj value;

        if (obj.hasAttr(pattern.getName())) {
            value = obj.getAttr(pattern.getName());
        }  else if (!usedValue && obj.hasAttr("value")) {
            value = obj.getAttr("value");
            usedValue = true;
        } else {
            return null;
        }

        // check later
        if (pattern.getObj().equals(value)) {
            return SUCCESS_NO_ASSIGNMENT;
        } else {
            return null;
        }
    }
}
