/*
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package xyz.avarel.kaiper.runtime.functions;

import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Tuple;
import xyz.avarel.kaiper.runtime.pattern.RuntimeLibPattern;
import xyz.avarel.kaiper.runtime.pattern.RuntimeLibPatternCase;
import xyz.avarel.kaiper.runtime.pattern.VariableRuntimeLibPattern;

import java.util.*;

public abstract class NativeFunc extends Func {
    private final RuntimeLibPatternCase pattern;

    public NativeFunc(String name) {
        this(name, new String[0]);
    }

    public NativeFunc(String name, RuntimeLibPattern... parameters) {
        super(name);
        this.pattern = new RuntimeLibPatternCase(Arrays.asList(parameters));
    }

    public NativeFunc(String name, String... params) {
        super(name);
        List<RuntimeLibPattern> patterns = new ArrayList<>(params.length);
        for (String param : params) {
            patterns.add(new VariableRuntimeLibPattern(param));
        }
        this.pattern = new RuntimeLibPatternCase(patterns);
    }

    @Override
    public int getArity() {
        return pattern.size();
    }

    public RuntimeLibPatternCase getPattern() {
        return pattern;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + getPattern() + ")" + " { native code... }";
    }

    @Override
    public Obj invoke(Tuple argument) {
        Map<String, Obj> scope = new HashMap<>(getArity());

        if (!new RuntimeLibPatternBinder(pattern, scope).bindFrom(argument)) {
            throw new ComputeException("Could not match arguments (" + argument + ") to " + getName() + "(" + pattern + ")");
        }

        Obj result = eval(scope);
        return result != null ? result : Null.VALUE;
    }

    protected abstract Obj eval(Map<String, Obj> arguments);
}
