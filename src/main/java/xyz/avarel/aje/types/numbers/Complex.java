package xyz.avarel.aje.types.numbers;

import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.NativeObject;
import xyz.avarel.aje.types.Type;
import xyz.avarel.aje.types.Truth;

import java.math.BigDecimal;

public class Complex implements Any<Complex>, NativeObject<Double> {
    public static final Type<Complex> TYPE = new Type<>(Numeric.TYPE, "complex");

    private final double re;
    private final double im;

    private Complex(double re) {
        this(re, 0);
    }

    private Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public static Complex of(double value) {
        return Complex.of(value, 0);
    }

    public static Complex of(double re, double im) {
        return new Complex(re, im);
    }

    @Override
    public Double toNative() {
        return re;
    }

    @Override
    public Type getType() {
        return TYPE;
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
    public Complex plus(Complex b) {
        return Complex.of(re + b.re, im + b.im);
    }

    @Override
    public Complex minus(Complex b) {
        return Complex.of(re - b.re, im - b.im);
    }

    @Override
    public Complex times(Complex b) {
        return Complex.of(re * b.re - im * b.im, re * b.im + im * b.re);
    }

    @Override
    public Complex divide(Complex b) {
        return times(b.reciprocal());
    }

    public Complex reciprocal() {
        double scale = re * re + im * im;
        return Complex.of(re / scale, -im / scale);
    }

    @Override
    public Complex pow(Complex other) {
        Complex result = ln().times(other).exp();
        double real = BigDecimal.valueOf(result.re).setScale(7, BigDecimal.ROUND_HALF_EVEN).doubleValue();
        double imag = BigDecimal.valueOf(result.im).setScale(7, BigDecimal.ROUND_HALF_EVEN).doubleValue();
        return Complex.of(real, imag);
    }

    public double abs() {
        if (re != 0 || im != 0) {
            return Math.sqrt(re * re + im * im);
        } else {
            return 0d;
        }
    }

    // Apache Commons Math
    public Complex ln() {
        return Complex.of(Math.log(abs()), arg());
    }

    // Apache Commons Math
    public Complex exp() {
        double exp = Math.exp(re);
        return Complex.of(exp * Math.cos(im), exp * Math.sin(im));
    }

    public double arg() {
        return Math.atan2(im, re);
    }

    public Complex conjugate() {
        return Complex.of(re, -im);
    }

    @Override
    public Complex negative() {
        return Complex.of(-re, -im);
    }

    public Complex ceil() {
        return Complex.of(Math.ceil(re), Math.ceil(im));
    }

    public Complex floor() {
        return Complex.of(Math.floor(re), Math.floor(im));
    }

    public Complex round() {
        return Complex.of(Math.round(re), Math.round(im));
    }

    @Override
    public Truth isEqualTo(Complex b) {
        return re == b.re && im == b.im ? Truth.TRUE : Truth.FALSE;
    }

    public double real() {
        return re;
    }

    public double imaginary() {
        return im;
    }
}
