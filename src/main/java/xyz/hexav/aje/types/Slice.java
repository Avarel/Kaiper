package xyz.hexav.aje.types;

import xyz.hexav.aje.AJEException;
import xyz.hexav.aje.types.interfaces.OperableValue;

public class Slice implements OperableValue<Slice> {
    private final double value;

    public Slice(double value) {
        this.value = value;
    }

    public static void assertIs(Object a) {
        if(!(a instanceof Slice)) {
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
    public Slice add(Slice other) {
        return new Slice(value + other.value);
    }

    @Override
    public Slice subtract(Slice other) {
        return new Slice(value - other.value);
    }

    @Override
    public Slice multiply(Slice other) {
        return new Slice(value * other.value);
    }

    @Override
    public Slice divide(Slice other) {
        return new Slice(value / other.value);
    }

    @Override
    public Slice mod(Slice other) {
        return new Slice((((value % other.value) + other.value) % other.value));
    }

    @Override
    public Slice negative() {
        return new Slice(-value);
    }

    @Override
    public Truth equals(Slice other) {
        return value == other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Truth greaterThan(Slice other) {
        return value > other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Truth lessThan(Slice other) {
        return value < other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Truth greaterThanOrEqual(Slice other) {
        return value >= other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Truth lessThanOrEqual(Slice other) {
        return value <= other.value ? Truth.TRUE : Truth.FALSE;
    }
}
