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
import xyz.avarel.kaiper.runtime.collections.Array;
import xyz.avarel.kaiper.runtime.pattern.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class RuntimeLibPatternBinder implements RuntimePatternVisitor<Pair<String, Obj>, Tuple> {
    private static final Pair<String, Obj> SUCCESS_NO_ASSIGNMENT = new Pair<>(null, null);

    private final Map<String, Obj> scope;

    private final RuntimeLibPatternCase patternCase;
    private int position = 0;

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
    public Pair<String, Obj> visit(RuntimeLibPatternCase patternCase, Tuple obj) {
        if (obj.hasAttr("_" + position)) {
            Obj value = obj.getAttr("_" + position);

            position++;

            Tuple tuple = value instanceof Tuple ? (Tuple) value : new Tuple(value);

            if (new RuntimeLibPatternBinder(patternCase, scope).bindFrom(tuple)) {
                return SUCCESS_NO_ASSIGNMENT;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public Pair<String, Obj> visit(WildcardRuntimeLibPattern pattern, Tuple obj) {
        position++;
        return SUCCESS_NO_ASSIGNMENT;
    }

    @Override
    public Pair<String, Obj> visit(ValueRuntimeLibPattern pattern, Tuple obj) {
        if (obj.hasAttr("_" + position)) {
            Obj value = obj.getAttr("_" + position);
            Obj target = pattern.getValue();
            position++;
            return value.equals(target) ? SUCCESS_NO_ASSIGNMENT : null;
        } else {
            return null;
        }
    }

    @Override
    public Pair<String, Obj> visit(VariableRuntimeLibPattern pattern, Tuple obj) {
        Obj value;

        if (obj.hasAttr(pattern.getName())) {
            value = obj.getAttr(pattern.getName());
        } else if (obj.hasAttr("_" + position)) {
            value = obj.getAttr("_" + position);
        } else {
            return null;
        }

        position++;
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
    public Pair<String, Obj> visit(RestRuntimeLibPattern pattern, Tuple obj) {
        Obj value;

        if (obj.hasAttr(pattern.getName())) {
            Obj val = obj.getAttr(pattern.getName());
            value = val instanceof Array ? val : Array.of(val);
        } else { // empty
            int endPosition = obj.size() - (patternCase.size() - (patternCase.getPatterns().indexOf(pattern) + 1));

            if (position < endPosition) {
                Array array = new Array();

                do {
                    array.add(obj.getAttr("_" + position));
                    position++;
                } while (position < endPosition);

                value = array;
            } else {
                return new Pair<>(pattern.getName(), new Array());
            }
        }

        return new Pair<>(pattern.getName(), value);
    }

    @Override
    public Pair<String, Obj> visit(TupleRuntimeLibPattern pattern, Tuple obj) {
        Obj value;

        if (obj.hasAttr(pattern.getName())) {
            value = obj.getAttr(pattern.getName());
        } else {
            return null;
        }

        position++;

        Tuple tuple = new Tuple(value);

        // check later
        return pattern.getPattern().accept(new RuntimeLibPatternBinder(patternCase, scope), tuple);
    }
}
