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

package xyz.avarel.aje.runtime.functions;

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Prototype;
import xyz.avarel.aje.runtime.Undefined;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Func implements Obj<Function<List<Obj>, Obj>> {
    public static final Prototype<Func> PROTOTYPE = new FunctionPrototype();

    public abstract int getArity();

    public abstract List<Parameter> getParameters();

    @Override
    public Prototype<Func> getType() {
        return PROTOTYPE;
    }

    @Override
    public Function<List<Obj>, Obj> toJava() {
        return this::invoke;
    }

    @Override
    public abstract Obj invoke(List<Obj> arguments);

    @Override
    public Obj plus(Obj other) {
        if (other instanceof Func) {
            return plus((Func) other);
        }
        return Undefined.VALUE;
    }

    private Func plus(Func right) {
        return new CombinedFunc(this, right, Obj::plus);
    }

    @Override
    public Obj minus(Obj other) {
        if (other instanceof Func) {
            return minus((Func) other);
        }
        return Undefined.VALUE;
    }

    private Func minus(Func right) {
        return new CombinedFunc(this, right, Obj::minus);
    }

    @Override
    public Obj times(Obj other) {
        if (other instanceof Func) {
            return times((Func) other);
        }
        return Undefined.VALUE;
    }

    private Func times(Func right) {
        return new CombinedFunc(this, right, Obj::times);
    }

    @Override
    public Obj divide(Obj other) {
        if (other instanceof Func) {
            return divide((Func) other);
        }
        return Undefined.VALUE;
    }

    private Func divide(Func right) {
        return new CombinedFunc(this, right, Obj::divide);
    }

    @Override
    public String toString() {
        return "func(" + getParameters().stream().map(Object::toString).collect(Collectors.joining(", ")) + ")";
    }

    public static class FunctionPrototype extends Prototype<Func> {
        public FunctionPrototype() {
            super("Function");
        }
    }
}
