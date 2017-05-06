package xyz.hexav.aje.types;

import xyz.hexav.aje.AJEException;
import xyz.hexav.aje.types.interfaces.ComparableValue;
import xyz.hexav.aje.types.interfaces.OperableValue;

public class Numeric implements OperableValue<Numeric>, ComparableValue<Numeric> {
    private final double value;

    public Numeric(double value) {
        this.value = value;
    }

    public static void assertIs(Object a) {
        if(!(a instanceof Numeric)) {
            throw new AJEException("Value needs to be a number.");
        }
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
        return new Numeric(value + other.value);
    }

    @Override
    public Numeric subtract(Numeric other) {
        if (other instanceof Complex) {
            return Complex.wrap(this).subtract(other);
        }
        return new Numeric(value - other.value);
    }

    @Override
    public Numeric multiply(Numeric other) {
        if (other instanceof Complex) {
            return Complex.wrap(this).multiply(other);
        }
        return new Numeric(value * other.value);
    }

    @Override
    public Numeric divide(Numeric other) {
        if (other instanceof Complex) {
            return Complex.wrap(this).divide(other);
        }
        return new Numeric(value / other.value);
    }

    @Override
    public Numeric pow(Numeric other) {
        if (other instanceof Complex) {
            return Complex.wrap(this).pow(other);
        }
        return new Numeric(Math.pow(value, other.value));
    }

    @Override
    public Numeric mod(Numeric other) {
        return new Numeric((((value % other.value) + other.value) % other.value));
    }

    @Override
    public Numeric negative() {
        return new Numeric(-value);
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
