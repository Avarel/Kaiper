package xyz.hexav.aje.types;

public class NumericValue implements Expression {
    private final double value;

    private NumericValue(double value) {
        this.value = value;
    }

    @Override
    public double value() {
        return value;
    }

    public static NumericValue of(double value) {
        return new NumericValue(value);
    }
}
