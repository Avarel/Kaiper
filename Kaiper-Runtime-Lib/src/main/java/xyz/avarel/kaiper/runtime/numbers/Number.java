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

package xyz.avarel.kaiper.runtime.numbers;

import xyz.avarel.kaiper.runtime.Bool;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.modules.Module;
import xyz.avarel.kaiper.runtime.modules.NativeModule;
import xyz.avarel.kaiper.runtime.types.Type;

public class Number implements Obj, Comparable<Number> {
    public static final Type<Number> TYPE = new Type<>("Number");
    public static final Module MODULE = new NativeModule("Number") {{
        declare("TYPE", Number.TYPE);
    }};
    private final double value;

    private Number(double value) {
        this.value = value;
    }

    public static Number of(double value) {
        return new Number(value);
    }

    public double value() {
        return value;
    }

    @Override
    public Double toJava() {
        return value();
    }

    @Override
    public Type<Number> getType() {
        return TYPE;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Obj) {
            return isEqualTo((Obj) obj) == Bool.TRUE;
        } else {
            return Double.valueOf(value) == obj;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }

    @Override
    public Obj plus(Obj other) {
        if (other instanceof Number) {
            return plus((Number) other);
        } else if (other instanceof Int) {
            return plus(Number.of(((Int) other).value()));
        }
        return Obj.super.plus(other);
    }

    private Number plus(Number other) {
        return Number.of(value + other.value);
    }

    @Override
    public Obj minus(Obj other) {
        if (other instanceof Number) {
            return minus((Number) other);
        } else if (other instanceof Int) {
            return minus(Number.of(((Int) other).value()));
        }
        return Obj.super.minus(other);
    }

    private Number minus(Number other) {
        return Number.of(value - other.value);
    }

    @Override
    public Obj times(Obj other) {
        if (other instanceof Number) {
            return times((Number) other);
        }  else if (other instanceof Int) {
            return times(Number.of(((Int) other).value()));
        }
        return Obj.super.times(other);
    }

    private Number times(Number other) {
        return Number.of(value * other.value);
    }

    @Override
    public Obj divide(Obj other) {
        if (other instanceof Number) {
            return divide((Number) other);
        }  else if (other instanceof Int) {
            return divide(Number.of(((Int) other).value()));
        }
        return Obj.super.divide(other);
    }

    public Number divide(Number other) {
        return Number.of(value / other.value);
    }

    @Override
    public Obj pow(Obj other) {
        if (other instanceof Number) {
            return pow((Number) other);
        } else if (other instanceof Int) {
            return pow(Number.of(((Int) other).value()));
        }
        return Obj.super.pow(other);
    }

    private Number pow(Number other) {
        return Number.of(Math.pow(value, other.value));
    }

    @Override
    public Obj mod(Obj other) {
        if (other instanceof Number) {
            return mod((Number) other);
        }  else if (other instanceof Int) {
            return mod(Number.of(((Int) other).value()));
        }
        return Obj.super.mod(other);
    }

    private Number mod(Number other) {
        return Number.of(Math.abs(value % other.value));
    }

    public Number negative() {
        return Number.of(-value);
    }

    @Override
    public Bool isEqualTo(Obj other) {
        if (other instanceof Number) {
            return isEqualTo((Number) other);
        } else if (other instanceof Int) {
            return isEqualTo(Number.of(((Int) other).value()));
        }
        return Bool.FALSE;
    }

    private Bool isEqualTo(Number other) {
        return Bool.of(value == other.value);
    }

    @Override
    public int compareTo(Obj other) {
        if (other instanceof Number) {
            return compareTo((Number) other);
        } else if (other instanceof Int) {
            return compareTo(Number.of(((Int) other).value()));
        }
        return Obj.super.compareTo(other);
    }

    @Override
    public int compareTo(Number other) {
        return Double.compare(value, other.value);
    }

    private Bool lessThan(Number other) {
        return Bool.of(value < other.value);
    }
}
