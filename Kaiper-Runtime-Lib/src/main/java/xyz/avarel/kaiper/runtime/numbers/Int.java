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
import xyz.avarel.kaiper.runtime.types.Type;

public class Int implements Obj, Comparable<Int> {
    public static final Type<Int> TYPE = new Type<>("Int");
    private final int value;

    private Int(int value) {
        this.value = value;
    }

    public static Int of(int i) {
        if (i >= IntCache.LOW && i <= IntCache.HIGH) {
            return IntCache.cache[i - IntCache.LOW];
        }
        return new Int(i);
    }

    public int value() {
        return value;
    }

    @Override
    public Integer toJava() {
        return value();
    }

    @Override
    public Type<Int> getType() {
        return TYPE;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Obj) {
            return isEqualTo((Obj) obj) == Bool.TRUE;
        } else {
            return Integer.valueOf(value) == obj;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public Obj plus(Obj other) {
        if (other instanceof Int) {
            return this.plus((Int) other);
        } else if (other instanceof Number) {
            return Number.of(value).plus(other);
        }
        return Obj.super.plus(other);
    }

    private Int plus(Int other) {
        return Int.of(value + other.value);
    }

    @Override
    public Obj minus(Obj other) {
        if (other instanceof Int) {
            return this.minus((Int) other);
        } else if (other instanceof Number) {
            return Number.of(value).minus(other);
        }
        return Obj.super.minus(other);
    }

    private Int minus(Int other) {
        return Int.of(value - other.value);
    }

    @Override
    public Obj times(Obj other) {
        if (other instanceof Int) {
            return this.times((Int) other);
        } else if (other instanceof Number) {
            return Number.of(value).minus(other);
        }
        return Obj.super.times(other);
    }

    private Int times(Int other) {
        return Int.of(value * other.value);
    }

    @Override
    public Obj divide(Obj other) {
        if (other instanceof Int) {
            return this.divide((Int) other);
        } else if (other instanceof Number) {
            return Number.of(value).divide(other);
        }
        return Obj.super.divide(other);
    }

    private Obj divide(Int other) {
        if (other.value == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return Int.of(value / other.value);
    }

    @Override
    public Obj pow(Obj other) {
        if (other instanceof Int) {
            return this.pow((Int) other);
        } else if (other instanceof Number) {
            return Number.of(value).pow(other);
        }
        return Obj.super.pow(other);
    }

    private Int pow(Int other) {
        return Int.of((int) Math.pow(value, other.value));
    }

    @Override
    public Obj mod(Obj other) {
        if (other instanceof Int) {
            return this.mod((Int) other);
        } else if (other instanceof Number) {
            return Number.of(value).mod(other);
        }
        return Obj.super.mod(other);
    }

    private Int mod(Int other) {
        return Int.of(Math.floorMod(value, other.value));
    }

    @Override
    public Int negative() {
        return Int.of(-value);
    }

    @Override
    public Bool isEqualTo(Obj other) {
        if (other instanceof Int) {
            return this.isEqualTo((Int) other);
        } else if (other instanceof Number) {
            return Number.of(value).isEqualTo(other);
        }
        return Bool.FALSE;
    }

    private Bool isEqualTo(Int other) {
        return Bool.of(value == other.value);
    }

    @Override
    public int compareTo(Obj other) {
        if (other instanceof Int) {
            return this.compareTo((Int) other);
        } else if (other instanceof Number) {
            return Number.of(value).compareTo(other);
        }
        return Obj.super.compareTo(other);
    }

    @Override
    public int compareTo(Int other) {
        return Integer.compare(value, other.value);
    }

    @Override
    public Obj shl(Obj other) {
        if (other instanceof Int) {
            return this.shl((Int) other);
        }
        return Obj.super.shl(other);
    }

    private Int shl(Int other) {
        return Int.of(value << other.value);
    }

    @Override
    public Obj shr(Obj other) {
        if (other instanceof Int) {
            return this.shr((Int) other);
        }
        return Obj.super.shr(other);
    }

    private Int shr(Int other) {
        return Int.of(value >> other.value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Obj> T as(Type<T> type) {
        if (type == Number.TYPE) {
            return (T) Number.of(value);
        }

        return Obj.super.as(type);
    }

    private static class IntCache {
        private static final int LOW = -128;
        private static final int HIGH = 127;
        private static final Int[] cache;

        static {
            cache = new Int[(HIGH - LOW) + 1];
            int counter = LOW;
            for (int i = 0; i < cache.length; i++) {
                cache[i] = new Int(counter++);
            }
        }

        private IntCache() {}
    }
}
