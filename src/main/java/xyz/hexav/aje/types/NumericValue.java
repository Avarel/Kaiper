package xyz.hexav.aje.types;

public class NumericValue implements Expression {
    private final double value;

    public NumericValue(double value) {
        this.value = value;
    }

    @Override
    public double value() {
        return value;
    }
}
