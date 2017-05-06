package xyz.hexav.aje.types;

public class ComplexNumeric implements OperableValue<ComplexNumeric>, ComparableValue<ComplexNumeric> {
    private final double real;
    private final double imag;

    public ComplexNumeric(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    @Override
    public double value() {
        return real;
    }

    @Override
    public String toString() {
        return String.valueOf(real);
    }

    @Override
    public ComplexNumeric add(ComplexNumeric other) {
        return new ComplexNumeric(real + other.real, imag + other.imag);
    }

    @Override
    public ComplexNumeric subtract(ComplexNumeric other) {
        return new ComplexNumeric(real - other.real, imag - other.imag);
    }

    @Override
    public ComplexNumeric multiply(ComplexNumeric other) {
        return new ComplexNumeric(real * other.real, imag);
    }

    @Override
    public ComplexNumeric divide(ComplexNumeric other) {
        return new ComplexNumeric(real / other.real, imag);
    }

    @Override
    public ComplexNumeric mod(ComplexNumeric other) {
        return new ComplexNumeric((((real % other.real) + other.real) % other.real), imag);
    }

    @Override
    public ComplexNumeric negative() {
        return new ComplexNumeric(-real, imag);
    }

    @Override
    public Truth equals(ComplexNumeric other) {
        return real == other.real ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Truth greaterThan(ComplexNumeric other) {
        return real > other.real ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Truth lessThan(ComplexNumeric other) {
        return real < other.real ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Truth greaterThanOrEqual(ComplexNumeric other) {
        return real >= other.real ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Truth lessThanOrEqual(ComplexNumeric other) {
        return real <= other.real ? Truth.TRUE : Truth.FALSE;
    }
}
