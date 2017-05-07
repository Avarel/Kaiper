package xyz.hexav.aje.types;

import xyz.hexav.aje.AJEException;
import xyz.hexav.aje.types.interfaces.ImplicitCasts;
import xyz.hexav.aje.types.interfaces.OperableValue;

import java.math.BigDecimal;

public class Complex implements OperableValue<Complex>, ImplicitCasts {
    private final double re;
    private final double im;

    public Complex(double re) {
        this(re, 0);
    }

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public static void assertIs(Object... objs) {
        for (Object a : objs) {
            if (!(a instanceof Complex)) {
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

    @Override
    public Double toNativeObject() {
        return re;
    }

    @Override
    public OperableValue[] implicitCastBy(OperableValue target) {
        OperableValue[] objs = new OperableValue[] { this, target };

        if (target instanceof Numeric) {
            objs[1] = Complex.of(((Numeric) target).value());
        } else if (target instanceof Slice) {
            objs[0] = new Slice(this);
        }

        return objs;
    }

    @Override
    public String getType() {
        return "complex";
    }

    @Override
    public String toString() {
        if (im == 0) return re + "";
        if (re == 0 && im == 1) return "i";
        if (re == 0) return im + "i";
        if (im < 0) return re + " - " + (-im) + "i";
        return re + " + " + im + "i";
    }

    @Override
    public Complex add(Complex b) {
        return new Complex(re + b.re, im + b.im);
    }

    @Override
    public Complex subtract(Complex b) {
        return new Complex(re - b.re, im - b.im);
    }

    @Override
    public Complex multiply(Complex b) {
        return new Complex(re * b.re - im * b.im, re * b.im + im * b.re);
    }

    @Override
    public Complex divide(Complex b) {
        return multiply(b.reciprocal());
    }

    public Complex reciprocal() {
        double scale = re * re + im * im;
        return new Complex(re / scale, -im / scale);
    }

    @Override
    public Complex root(Complex other) {
        return other.pow(this.reciprocal());
    }

    @Override
    public Complex pow(Complex other) {
        Complex result = log().multiply(other).exp();
        double real = BigDecimal.valueOf(result.re).setScale(7, BigDecimal.ROUND_HALF_EVEN).doubleValue();
        double imag = BigDecimal.valueOf(result.im).setScale(7, BigDecimal.ROUND_HALF_EVEN).doubleValue();
        return new Complex(real, imag);
    }

    // Apache Commons Math
    public double abs() {
        if (Math.abs(re) < Math.abs(im)) {
            if (im == 0.0) {
                return Math.abs(re);
            }
            double q = re / im;
            return Math.abs(im) * Math.sqrt(1 + q * q);
        } else {
            if (re == 0.0) {
                return Math.abs(im);
            }
            double q = im / re;
            return Math.abs(re) * Math.sqrt(1 + q * q);
        }
    }

    // Apache Commons Math
    public Complex log() {
        return new Complex(Math.log(abs()), Math.atan2(im, re));
    }

    // Apache Commons Math
    private Complex exp() {
        double exp = Math.exp(re);
        return new Complex(exp * Math.cos(im), exp * Math.sin(im));
    }

    @Override
    public Complex negative() {
        return new Complex(-re, -im);
    }

    @Override
    public Truth equals(Complex b) {
        return re == b.re && im == b.im ? Truth.TRUE : Truth.FALSE;
    }
}
