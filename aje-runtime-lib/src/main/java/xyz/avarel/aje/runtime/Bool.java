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
import xyz.avarel.aje.runtime.modules.Module;
import xyz.avarel.aje.runtime.modules.NativeModule;
import xyz.avarel.aje.runtime.types.Type;

import java.util.List;

public enum Bool implements Obj {
    TRUE(true),
    FALSE(false);

    public static final Type<Bool> TYPE = new Type<>("Boolean");

    public static final Module MODULE = new NativeModule() {{
        declare("TYPE", Bool.TYPE);

        declare("parse", new NativeFunc("parse", "a") {
            @Override
            protected Obj eval(List<Obj> arguments) {
                Obj obj = arguments.get(0);
                if (obj instanceof Bool) {
                    return obj;
                }
                return Bool.of(Boolean.valueOf(obj.toString()));
            }
        });
    }};

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
    public Type getType() {
        return TYPE;
    }

    @Override
    public Obj or(Obj other) {
        if (other instanceof Bool) {
            return or((Bool) other);
        }
        return Obj.super.shr(other);
    }

    public Bool or(Bool other) {
        return Bool.of(value || other.value);
    }

    @Override
    public Obj and(Obj other) {
        if (other instanceof Bool) {
            return and((Bool) other);
        }
        return Obj.super.and(other);
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
        return Obj.super.pow(other);
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
}
