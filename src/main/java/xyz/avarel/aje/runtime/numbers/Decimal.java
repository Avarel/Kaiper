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

public class Decimal implements Obj<Double> {
    public static final Type<Decimal> TYPE = new Type<>(Complex.TYPE, "decimal");

    private final double value;

    private Decimal(double value) {
        this.value = value;
    }

    public static Decimal of(double value) {
        return new Decimal(value);
    }

    public double value() {
        return value;
    }

    @Override
    public Double toNative() {
        return value();
    }

    @Override
    public Type<Decimal> getType() {
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
        if (other instanceof Decimal) {
            return plus((Decimal) other);
        } else if (other instanceof Int) {
            return plus(Decimal.of(((Int) other).value()));
        } else if (other instanceof Complex) {
            return Complex.of(value).plus(other);
        }
        return Undefined.VALUE;
    }

    private Decimal plus(Decimal other) {
        return Decimal.of(value + other.value);
    }

    @Override
    public Obj minus(Obj other) {
        if (other instanceof Decimal) {
            return minus((Decimal) other);
        } else if (other instanceof Int) {
            return minus(Decimal.of(((Int) other).value()));
        } else if (other instanceof Complex) {
            return Complex.of(value).minus(other);
        }
        return Undefined.VALUE;
    }

    private Decimal minus(Decimal other) {
        return Decimal.of(value - other.value);
    }

    @Override
    public Obj times(Obj other) {
        if (other instanceof Decimal) {
            return times((Decimal) other);
        } else if (other instanceof Int) {
            return times(Decimal.of(((Int) other).value()));
        } else if (other instanceof Complex) {
            return Complex.of(value).times(other);
        }
        return Undefined.VALUE;
    }

    private Decimal times(Decimal other) {
        return Decimal.of(value * other.value);
    }

    @Override
    public Obj divide(Obj other) {
        if (other instanceof Decimal) {
            return divide((Decimal) other);
        } else if (other instanceof Int) {
            return divide(Decimal.of(((Int) other).value()));
        } else if (other instanceof Complex) {
            return Complex.of(value).divide(other);
        }
        return Undefined.VALUE;
    }

    public Decimal divide(Decimal other) {
        return Decimal.of(value / other.value);
    }

    @Override
    public Obj pow(Obj other) {
        if (other instanceof Decimal) {
            return pow((Decimal) other);
        } else if (other instanceof Int) {
            return pow(Decimal.of(((Int) other).value()));
        } else if (other instanceof Complex) {
            return Complex.of(value).pow(other);
        }
        return Undefined.VALUE;
    }

    private Decimal pow(Decimal other) {
        return Decimal.of(Math.pow(value, other.value));
    }

    @Override
    public Obj mod(Obj other) {
        if (other instanceof Decimal) {
            return mod((Decimal) other);
        } else if (other instanceof Int) {
            return mod(Decimal.of(((Int) other).value()));
        } else if (other instanceof Complex) {
            return Complex.of(value).mod(other);
        }
        return Undefined.VALUE;
    }

    private Decimal mod(Decimal other) {
        return Decimal.of((value % other.value + other.value) % other.value);
    }

    public Decimal negative() {
        return Decimal.of(-value);
    }

    @Override
    public Obj isEqualTo(Obj other) {
        if (other instanceof Decimal) {
            return this.isEqualTo((Decimal) other);
        } else if (other instanceof Int) {
            return this.isEqualTo(Decimal.of(((Int) other).value()));
        } else if (other instanceof Complex) {
            return Complex.of(value).isEqualTo(other);
        }
        return Bool.FALSE;
    }

    private Obj isEqualTo(Decimal other) {
        return Bool.of(value == other.value);
    }

    @Override
    public Obj greaterThan(Obj other) {
        if (other instanceof Decimal) {
            return this.greaterThan((Decimal) other);
        } else if (other instanceof Int) {
            return this.greaterThan(Decimal.of(((Int) other).value()));
        } else if (other instanceof Complex) {
            return Complex.of(value).greaterThan(other);
        }
        return Bool.FALSE;
    }

    private Obj greaterThan(Decimal other) {
        return Bool.of(value > other.value);
    }

    @Override
    public Obj lessThan(Obj other) {
        if (other instanceof Decimal) {
            return this.lessThan((Decimal) other);
        } else if (other instanceof Int) {
            return this.lessThan(Decimal.of(((Int) other).value()));
        } else if (other instanceof Complex) {
            return Complex.of(value).lessThan(other);
        }
        return Bool.FALSE;
    }

    private Bool lessThan(Decimal other) {
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
                return Int.of((int) value);
            case "toDecimal":
                return this;
            case "toComplex":
                return Complex.of(value);
        }
        return Obj.super.getAttr(name);
    }
}
