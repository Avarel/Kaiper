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

import xyz.avarel.kaiper.Pair;
import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.interpreter.ExprInterpreter;
import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Tuple;
import xyz.avarel.kaiper.scope.Scope;

import java.util.LinkedHashMap;
import java.util.Map;

public class PatternBinder implements PatternVisitor<Pair<String, Obj>, Tuple> {
    // dummy
    private static final Pair<String, Obj> SUCCESS_NO_ASSIGNMENT = new Pair<>(null, null);

    private final PatternCase patternCase;
    private final ExprInterpreter interpreter;
    private final Scope scope;

    private boolean usedValue = false;

    public PatternBinder(PatternCase patternCase, ExprInterpreter interpreter, Scope scope) {
        this.patternCase = patternCase;
        this.interpreter = interpreter;
        this.scope = scope;
    }

    public boolean declareFrom(Tuple tuple) {
        Map<String, Obj> results = bindingsFrom(tuple);

        if (results == null) return false;

        for (Map.Entry<String, Obj> result : results.entrySet()) {
            ExprInterpreter.declare(scope, result.getKey(), result.getValue());
        }

        return true;
    }

    public boolean assignFrom(Tuple tuple) {
        Map<String, Obj> results = bindingsFrom(tuple);

        if (results == null) return false;

        for (Map.Entry<String, Obj> result : results.entrySet()) {
            if(!ExprInterpreter.assign(scope, result.getKey(), result.getValue())) {
                throw new ComputeException(result.getKey() + " is not defined, it must be declared first");
            }
        }

        return true;
    }

    private Map<String, Obj> bindingsFrom(Tuple tuple) {
        Map<String, Obj> results = new LinkedHashMap<>();
        for (Pattern pattern : patternCase.getPatterns()) {
            Pair<String, Obj> result = pattern.accept(this, tuple);

            if (result != null) {
                if (result != SUCCESS_NO_ASSIGNMENT) {
                    results.put(result.getFirst(), result.getSecond());
                }
            } else {
                return null;
            }
        }

        return results;
    }

    @Override
    public Pair<String, Obj> visit(VariablePattern pattern, Tuple obj) {
        Obj value;

        if (obj.hasAttr(pattern.getName())) {
            value = obj.getAttr(pattern.getName());
        } else if (!usedValue && obj.hasAttr("value")) {
            value = obj.getAttr("value");
            usedValue = true;
        } else {
            return null;
        }

        if (!pattern.isNullable() && value == Null.VALUE) {
            return null;
        } else {
            return new Pair<>(pattern.getName(), value);
        }
    }

    @Override
    public Pair<String, Obj> visit(DefaultPattern pattern, Tuple obj) {
        Pair<String, Obj> result = pattern.getDelegate().accept(this, obj);

        if (result == null) {
            Obj value = pattern.getDefault().accept(interpreter, scope);
            return new Pair<>(pattern.getDelegate().getName(), value);
        } else {
            return result;
        }
    }

    @Override
    public Pair<String, Obj> visit(TuplePattern pattern, Tuple obj) {
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
        if (interpreter.resultOf(pattern.getExpr(), scope).equals(value)) {
            return SUCCESS_NO_ASSIGNMENT;
        } else {
            return null;
        }
    }
}
