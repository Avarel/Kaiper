package xyz.hexav.aje.types;

import xyz.hexav.aje.AJEException;
import xyz.hexav.aje.types.interfaces.OperableValue;

import java.math.BigDecimal;

public class Complex extends Numeric implements OperableValue<Numeric> {
    private final double im;

    public Complex(double re) {
        this(re, 0);
    }

    public Complex(double re, double im) {
        super(re);
        this.im = im;
    }

    public static void assertIs(Object... objs) {
        for (Object a : objs) {
            if(!(a instanceof Complex)) {
                throw new AJEException("Value needs to be a complex number.");
            }
        }
    }

    public static Complex of(double value) {
        return Complex.of(value, 0);
    }

    public static Complex of(double re, double im) {
        return new Complex(re, im);
    }

    public static Complex wrap(Numeric a) {
        if (a instanceof Complex) return (Complex) a;
        return new Complex(a.value());
    }

    @Override
    public String getType() {
        return "complex";
    }

    @Override
    public String toString() {
        if (im == 0) return value() + "";
        if (value() == 0 && im == 1) return "i";
        if (value() == 0) return im + "i";
        if (im <  0) return value() + " - " + (-im) + "i";
        return value() + " + " + im + "i";
    }

    @Override
    public Complex add(Numeric other) {
        return add(wrap(other));
    }

    public Complex add(Complex b) {
        return new Complex(value() + b.value(), im + b.im);
    }

    @Override
    public Complex subtract(Numeric other) {
        return subtract(wrap(other));
    }

    public Complex subtract(Complex b) {
        return new Complex(value() - b.value(), im - b.im);
    }

    @Override
    public Complex multiply(Numeric other) {
        return multiply(wrap(other));
    }

    public Complex multiply(Complex b) {
        return new Complex(value() * b.value() - im * b.im, value() * b.im + im * b.value());
    }

    @Override
    public Complex divide(Numeric other) {
        return divide(wrap(other));
    }

    public Complex divide(Complex b) {
        return multiply(b.reciprocal());
    }

    public Complex reciprocal() {
        double scale = value() * value() + im*im;
        return new Complex(value() / scale, -im / scale);
    }

    @Override
    public Complex mod(Numeric b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Complex pow(Numeric other) {
        return pow(wrap(other));
    }

    public Complex pow(Complex other) {
        Complex result = log().multiply(other).exp();
        double real = BigDecimal.valueOf(result.value()).setScale(7, BigDecimal.ROUND_HALF_EVEN).doubleValue();
        double imag = BigDecimal.valueOf(result.im).setScale(7, BigDecimal.ROUND_HALF_EVEN).doubleValue();
        return new Complex(real, imag);
    }

    // Apache Commons Math
    public double abs() {
        if (Math.abs(value()) < Math.abs(im)) {
            if (im == 0.0) {
                return Math.abs(value());
            }
            double q = value() / im;
            return Math.abs(im) * Math.sqrt(1 + q * q);
        } else {
            if (value() == 0.0) {
                return Math.abs(im);
            }
            double q = im / value();
            return Math.abs(value()) * Math.sqrt(1 + q * q);
        }
    }

    // Apache Commons Math
    public Complex log() {
        return new Complex(Math.log(abs()), Math.atan2(im, value()));
    }

    // Apache Commons Math
    private Complex exp() {
        double exp = Math.exp(value());
        return new Complex(exp *  Math.cos(im), exp * Math.sin(im));
    }

    @Override
    public Complex negative() {
        return new Complex(-value(), -im);
    }

    @Override
    public Truth equals(Numeric other) {
        return equals(wrap(other));
    }

    public Truth equals(Complex b) {
        return value() == b.value() && im == b.im ? Truth.TRUE : Truth.FALSE;
    }
}
