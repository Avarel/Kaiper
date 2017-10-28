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

import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.exceptions.ReturnException;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.pattern.RuntimePatternCase;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

public class RuntimeMultimethod extends Func {
    private final SortedMap<RuntimePatternCase, Function<Map<String, Obj>, Obj>> methodCases = new TreeMap<>();

    public RuntimeMultimethod(String name) {
        super(name);
    }

    public Map<RuntimePatternCase, Function<Map<String, Obj>, Obj>> getMethodCases() {
        return methodCases;
    }

    public RuntimeMultimethod addCase(RuntimePatternCase pattern, Function<Map<String, Obj>, Obj> consumer) {
        if (methodCases.put(pattern, consumer) != null) {
            throw new ComputeException("Internal: duplicate definition for method " + getName());
        }

        return this;
    }

    @Override
    public Obj invoke(Obj argument) {
        for (Map.Entry<RuntimePatternCase, Function<Map<String, Obj>, Obj>> entry : methodCases.entrySet()) {
            Map<String, Obj> scope = new HashMap<>();

            if (!new RuntimePatternBinder(entry.getKey()).bindFrom(scope, argument)) {
                continue;
            }

            try {
                return entry.getValue().apply(scope);
            } catch (ReturnException re) {
                return re.getValue();
            }
        }

        throw new ComputeException("Could not match arguments (" + argument + ") to any method cases");
    }

    @Override
    public String toString() {
        return super.toString() + "(" + (methodCases.size() == 1 ? methodCases.firstKey() : methodCases.size() + " definitions") + ")";
    }
}
