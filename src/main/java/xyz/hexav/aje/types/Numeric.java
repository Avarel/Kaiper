package xyz.hexav.aje.types;

import xyz.hexav.aje.AJEException;
import xyz.hexav.aje.types.interfaces.OperableValue;

import java.math.BigDecimal;

public class Numeric implements OperableValue<Numeric> {
    private final double value;

    public Numeric(double value) {
        this.value = value;
    }

    public static void assertIs(Object... objs) {
        for (Object a : objs) {
            if(!(a instanceof Numeric)) {
                throw new AJEException("Value needs to be a number.");
            }
        }
    }

    public static Numeric of(double value) {
        return new Numeric(value);
    }

    @Override
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
    public Numeric add(Numeric other) {
        if (other instanceof Complex) {
            return Complex.wrap(this).add(other);
        }
        return Numeric.of(value + other.value);
    }

    @Override
    public Numeric subtract(Numeric other) {
        if (other instanceof Complex) {
            return Complex.wrap(this).subtract(other);
        }
        return Numeric.of(value - other.value);
    }

    @Override
    public Numeric multiply(Numeric other) {
        if (other instanceof Complex) {
            return Complex.wrap(this).multiply(other);
        }
        return Numeric.of(value * other.value);
    }

    @Override
    public Numeric divide(Numeric other) {
        if (other instanceof Complex) {
            return Complex.wrap(this).divide(other);
        }
        return Numeric.of(value / other.value);
    }

    @Override
    public Numeric pow(Numeric other) {
        if (other instanceof Complex) {
            return Complex.wrap(this).pow(other);
        }
        return Numeric.of(Math.pow(value, other.value));
    }

    @Override
    public Numeric root(Numeric other) {
        if (other instanceof Complex) {
            return other.pow(Complex.of(1 / value));
        }

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
