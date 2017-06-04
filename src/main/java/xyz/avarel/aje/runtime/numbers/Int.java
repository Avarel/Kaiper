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

package xyz.avarel.aje.runtime.numbers;

import xyz.avarel.aje.runtime.Bool;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Type;
import xyz.avarel.aje.runtime.Undefined;

import java.util.List;

public class Int implements Obj<Integer> {
    public static final Type<Int> TYPE = new Type<>(Decimal.TYPE, "integer");

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
    public Integer toNative() {
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
        } else if (other instanceof Decimal) {
            return Decimal.of(value).plus(other);
        } else if (other instanceof Complex) {
            return Complex.of(value).plus(other);
        }
        return Undefined.VALUE;
    }

    private Int plus(Int other) {
        return Int.of(value + other.value);
    }

    @Override
    public Obj minus(Obj other) {
        if (other instanceof Int) {
            return this.minus((Int) other);
        } else if (other instanceof Decimal) {
            return Decimal.of(value).minus(other);
        } else if (other instanceof Complex) {
            return Complex.of(value).minus(other);
        }
        return Undefined.VALUE;
    }

    private Int minus(Int other) {
        return Int.of(value - other.value);
    }

    @Override
    public Obj times(Obj other) {
        if (other instanceof Int) {
            return this.times((Int) other);
        } else if (other instanceof Decimal) {
            return Decimal.of(value).times(other);
        } else if (other instanceof Complex) {
            return Complex.of(value).times(other);
        }
        return Undefined.VALUE;
    }

    private Int times(Int other) {
        return Int.of(value * other.value);
    }

    @Override
    public Obj divide(Obj other) {
        if (other instanceof Int) {
            return this.divide((Int) other);
        } else if (other instanceof Decimal) {
            return Decimal.of(value).divide(other);
        } else if (other instanceof Complex) {
            return Complex.of(value).divide(other);
        }
        return Undefined.VALUE;
    }

    private Obj divide(Int other) {
        if (other.value == 0) {
            return Decimal.of(value).divide(other);
        }
        return Int.of(value / other.value);
    }

    @Override
    public Obj pow(Obj other) {
        if (other instanceof Int) {
            return this.pow((Int) other);
        } else if (other instanceof Decimal) {
            return Decimal.of(value).pow(other);
        } else if (other instanceof Complex) {
            return Complex.of(value).pow(other);
        }
        return Undefined.VALUE;
    }

    private Int pow(Int other) {
        return Int.of((int) Math.pow(value, other.value));
    }

    @Override
    public Obj mod(Obj other) {
        if (other instanceof Int) {
            return this.mod((Int) other);
        } else if (other instanceof Decimal) {
            return Decimal.of(value).mod(other);
        } else if (other instanceof Complex) {
            return Complex.of(value).mod(other);
        }
        return Undefined.VALUE;
    }

    private Int mod(Int other) {
        return Int.of(Math.floorMod(value, other.value));
    }

    @Override
    public Int negative() {
        return Int.of(-value);
    }

    @Override
    public Obj isEqualTo(Obj other) {
        if (other instanceof Int) {
            return this.isEqualTo((Int) other);
        } else if (other instanceof Decimal) {
            return Decimal.of(value).isEqualTo(other);
        } else if (other instanceof Complex) {
            return Complex.of(value).isEqualTo(other);
        }
        return Bool.FALSE;
    }

    private Obj isEqualTo(Int other) {
        return Bool.of(value == other.value);
    }

    @Override
    public Obj greaterThan(Obj other) {
        if (other instanceof Int) {
            return this.greaterThan((Int) other);
        } else if (other instanceof Decimal) {
            return Decimal.of(value).greaterThan(other);
        } else if (other instanceof Complex) {
            return Complex.of(value).greaterThan(other);
        }
        return Bool.FALSE;
    }

    private Obj greaterThan(Int other) {
        return Bool.of(value > other.value);
    }

    @Override
    public Obj lessThan(Obj other) {
        if (other instanceof Int) {
            return this.lessThan((Int) other);
        } else if (other instanceof Decimal) {
            return Decimal.of(value).lessThan(other);
        } else if (other instanceof Complex) {
            return Complex.of(value).lessThan(other);
        }
        return Bool.FALSE;
    }

    private Obj lessThan(Int other) {
        return Bool.of(value < other.value);
    }

    @Override
    public Obj invoke(Obj receiver, List<Obj> arguments) {
        if (receiver == Undefined.VALUE && arguments.size() == 1) {
            return times(arguments.get(0));
        }
        return Undefined.VALUE;
    }

    @Override
    public Obj getAttr(String name) {
        switch (name) {
            case "toInteger":
                return this;
            case "toDecimal":
                return Decimal.of(value);
            case "toComplex":
                return Complex.of(value);
        }
        return Obj.super.getAttr(name);
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
