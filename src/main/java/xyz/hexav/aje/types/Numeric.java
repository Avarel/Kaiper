package xyz.hexav.aje.types;

import xyz.hexav.aje.AJEException;
import xyz.hexav.aje.types.interfaces.ImplicitCasts;
import xyz.hexav.aje.types.interfaces.OperableValue;

import java.math.BigDecimal;

public class Numeric implements OperableValue<Numeric>, ImplicitCasts {
    private final double value;

    public Numeric(double value) {
        this.value = value;
    }

    public static void assertIs(Object... objs) {
        for (Object a : objs) {
            if (!(a instanceof Numeric)) {
                throw new AJEException("Value needs to be a number.");
            }
        }
    }

    public static Numeric of(double value) {
        return new Numeric(value);
    }

    public double value() {
        return value;
    }

    @Override
    public Double toNativeObject() {
        return value();
    }

    @Override
    public String getType() {
        return "number";
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public OperableValue[] implicitCastBy(OperableValue target) {
        OperableValue[] objs = new OperableValue[] { this, target };

        if (target instanceof Complex) {
            objs[0] = Complex.of(value);
        } else if (target instanceof Slice) {
            objs[0] = new Slice(this);
        }

        return objs;
    }

    @Override
    public Numeric add(Numeric other) {
        return Numeric.of(value + other.value);
    }

    @Override
    public Numeric subtract(Numeric other) {
        return Numeric.of(value - other.value);
    }

    @Override
    public Numeric multiply(Numeric other) {
        return Numeric.of(value * other.value);
    }

    @Override
    public Numeric divide(Numeric other) {
        return Numeric.of(value / other.value);
    }

    @Override
    public Numeric pow(Numeric other) {
        return Numeric.of(Math.pow(value, other.value));
    }

    @Override
    public Numeric root(Numeric other) {
        if (value == 1) {
            return other;
        } else if (value == 2) {
            return Numeric.of(Math.sqrt(other.value));
        } else if (value == 3) {
            return Numeric.of(Math.cbrt(other.value));
        } else {
            double result = Math.pow(value, 1.0 / other.value);
            double val = BigDecimal.valueOf(result).setScale(7, BigDecimal.ROUND_HALF_EVEN).doubleValue();
            return Numeric.of(val);
        }
    }

    @Override
    public Numeric mod(Numeric other) {
        Numeric n = Numeric.of((value % other.value + other.value) % other.value);
        System.out.println(n);
        return n;
    }

    @Override
    public Numeric negative() {
        return Numeric.of(-value);
    }

    @Override
    public Truth equals(Numeric other) {
        return value == other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Truth greaterThan(Numeric other) {
        return value > other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Truth lessThan(Numeric other) {
        return value < other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Truth greaterThanOrEqual(Numeric other) {
        return value >= other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Truth lessThanOrEqual(Numeric other) {
        return value <= other.value ? Truth.TRUE : Truth.FALSE;
    }
}
