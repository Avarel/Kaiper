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
import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.runtime_pattern.Pattern;
import xyz.avarel.kaiper.runtime.runtime_pattern.PatternCase;
import xyz.avarel.kaiper.runtime.runtime_pattern.VariablePattern;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class NativeFunc extends Func {
    private final PatternCase pattern;

    public NativeFunc(String name) {
        this(name, new String[0]);
    }

    public NativeFunc(String name, Pattern... parameters) {
        this(name, new PatternCase(Arrays.asList(parameters)));
    }

    public NativeFunc(String name, String... params) {
        this(name, new PatternCase(Arrays.stream(params).map(VariablePattern::new).toArray(VariablePattern[]::new)));
    }

    public NativeFunc(String name, PatternCase pattern) {
        super(name);
        this.pattern = pattern;
    }

    public PatternCase getPattern() {
        return pattern;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + getPattern() + ")" + " { native code... }";
    }

    @Override
    public Obj invoke(Obj argument) {
        Map<String, Obj> scope = new HashMap<>();

        if (!new RuntimePatternBinder(scope).bind(pattern, argument)) {
            throw new ComputeException("Could not match arguments (" + argument + ") to " + getName() + "(" + pattern + ")");
        }

        Obj result = eval(scope);
        return result != null ? result : Null.VALUE;
    }

    protected abstract Obj eval(Map<String, Obj> arguments);
}
