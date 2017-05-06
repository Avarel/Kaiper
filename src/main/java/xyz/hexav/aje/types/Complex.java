package xyz.hexav.aje.types;

public class Complex extends Numeric implements OperableValue<Numeric> {
    private final double re;
    private final double im;

    public Complex(double re, double im) {
        super(re);
        this.re = re;
        this.im = im;
    }

    public static boolean check(Object a) {
        return a instanceof Complex;
    }

    public static Complex wrap(Object a) {
        return (Complex) a;
    }

    public static Complex wrap(Numeric a) {
        if (check(a)) return (Complex) a;
        return new Complex(a.value(), 0);
    }

    @Override
    public double value() {
        return re;
    }

    @Override
    public String toString() {
        if (im == 0) return re + "";
        if (re == 0 && im == 1) return "i";
        if (re == 0) return im + "i";
        if (im <  0) return re + " - " + (-im) + "i";
        return re + " + " + im + "i";
    }

    @Override
    public Complex add(Numeric other) {
        return add(wrap(other));
    }

    public Complex add(Complex b) {
        return new Complex(re + b.re, im + b.im);
    }

    @Override
    public Complex subtract(Numeric other) {
        return subtract(wrap(other));
    }

    public Complex subtract(Complex b) {
        return new Complex(re - b.re, im - b.im);
    }

    @Override
    public Complex multiply(Numeric other) {
        return multiply(wrap(other));
    }

    public Complex multiply(Complex b) {
        return new Complex(re * b.re - im * b.im, re * b.im + im * b.re);
    }

    @Override
    public Complex divide(Numeric other) {
        return divide(wrap(other));
    }

    public Complex divide(Complex b) {
        return multiply(b.reciprocal());
    }

    public Complex reciprocal() {
        double scale = re*re + im*im;
        return new Complex(re / scale, -im / scale);
    }

    @Override
    public Complex mod(Numeric b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Complex negative() {
        return new Complex(-re, -im);
    }

    @Override
    public Truth equals(Numeric other) {
        return equals(wrap(other));
    }

    public Truth equals(Complex b) {
        return re == b.re && im == b.im ? Truth.TRUE : Truth.FALSE;
    }
}
