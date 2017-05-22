package xyz.avarel.aje.runtime.numbers;

import xyz.avarel.aje.runtime.*;

import java.util.List;

public class Decimal implements Any, NativeObject<Double> {
    public static final Type<Decimal> TYPE = new Type<>(Numeric.TYPE, "decimal");

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
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public Any plus(Any other) {
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
    public Any minus(Any other) {
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
    public Any times(Any other) {
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
    public Any divide(Any other) {
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
    public Any pow(Any other) {
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
    public Any mod(Any other) {
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
    public boolean equals(Object obj) {
        if (obj instanceof Any) {
            return isEqualTo((Any) obj) == Truth.TRUE;
        } else {
            return Double.valueOf(value) == obj;
        }
    }

    @Override
    public Any isEqualTo(Any other) {
        if (other instanceof Decimal) {
            return this.isEqualTo((Decimal) other);
        } else if (other instanceof Int) {
            return this.isEqualTo(Decimal.of(((Int) other).value()));
        } else if (other instanceof Complex) {
            return Complex.of(value).isEqualTo(other);
        }
        return Truth.FALSE;
    }

    private Any isEqualTo(Decimal other) {
        return value == other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Any greaterThan(Any other) {
        if (other instanceof Decimal) {
            return this.greaterThan((Decimal) other);
        } else if (other instanceof Int) {
            return this.greaterThan(Decimal.of(((Int) other).value()));
        } else if (other instanceof Complex) {
            return Complex.of(value).greaterThan(other);
        }
        return Truth.FALSE;
    }

    private Any greaterThan(Decimal other) {
        return value > other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Any lessThan(Any other) {
        if (other instanceof Decimal) {
            return this.lessThan((Decimal) other);
        } else if (other instanceof Int) {
            return this.lessThan(Decimal.of(((Int) other).value()));
        } else if (other instanceof Complex) {
            return Complex.of(value).lessThan(other);
        }
        return Truth.FALSE;
    }

    private Truth lessThan(Decimal other) {
        return value < other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Any invoke(List<Any> args) {
        if (args.size() == 1) {
            return times(args.get(0));
        }
        return Undefined.VALUE;
    }
}
