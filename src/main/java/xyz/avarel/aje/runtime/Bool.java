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

package xyz.avarel.aje.runtime;

import xyz.avarel.aje.runtime.functions.NativeFunc;
import xyz.avarel.aje.runtime.functions.Parameter;
import xyz.avarel.aje.runtime.numbers.Int;

import java.util.List;

public enum Bool implements Obj<Boolean> {
    TRUE(true),
    FALSE(false);

    public static final Cls<Bool> CLS = new BoolCls();

    private final boolean value;

    Bool(boolean value) {
        this.value = value;
    }

    public static Bool of(boolean value) {
        return value ? TRUE : FALSE;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

    @Override
    public Boolean toJava() {
        return value;
    }

    @Override
    public Cls getType() {
        return CLS;
    }

    @Override
    public Obj or(Obj other) {
        if (other instanceof Bool) {
            return or((Bool) other);
        }
        return Undefined.VALUE;
    }

    public Bool or(Bool other) {
        return Bool.of(value || other.value);
    }

    @Override
    public Obj and(Obj other) {
        if (other instanceof Bool) {
            return and((Bool) other);
        }
        return Undefined.VALUE;
    }

    public Bool and(Bool other) {
        return Bool.of(value && other.value);
    }

    @Override
    public Bool negate() {
        return Bool.of(!value);
    }

    @Override
    public Obj pow(Obj other) {
        if (other instanceof Bool) {
            return pow((Bool) other);
        }
        return Undefined.VALUE;
    }

    public Bool pow(Bool other) {
        return Bool.of(value ^ other.value);
    }

    @Override
    public Bool isEqualTo(Obj other) {
        if (other instanceof Bool) {
            return Bool.of(value == ((Bool) other).value);
        }
        return FALSE;
    }

    @Override
    public Obj getAttr(String name) {
        switch (name) {
            case "int":
                return value ? Int.of(1) : Int.of(0);
            default:
                return Obj.super.getAttr(name);
        }
    }

    private static class BoolCls extends Cls<Bool> {
        public BoolCls() {
            super("Boolean");

            getScope().declare("toInt", new NativeFunc(Parameter.of("self")) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return Int.of(((Bool) arguments.get(0)).value ? 1 : 0);
                }
            });
        }
    }
}
