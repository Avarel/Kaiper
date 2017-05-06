package xyz.hexav.aje.types;

public class Numeric implements OperableValue<Numeric>, ComparableValue<Numeric> {
    private final double value;

    public Numeric(double value) {
        this.value = value;
    }

    @Override
    public double value() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public Numeric add(Numeric other) {
        return new Numeric(value + other.value);
    }

    @Override
    public Numeric subtract(Numeric other) {
        return new Numeric(value - other.value);
    }

    @Override
    public Numeric multiply(Numeric other) {
        return new Numeric(value * other.value);
    }

    @Override
    public Numeric divide(Numeric other) {
        return new Numeric(value / other.value);
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
