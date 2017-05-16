package xyz.avarel.aje.runtime.numbers;

import xyz.avarel.aje.runtime.*;

import java.math.BigDecimal;

public class Complex implements Any, NativeObject<Double> {
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
    public Type<Complex> getType() {
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
    public Any plus(Any other) {
        if (other instanceof Complex) {
            return this.plus((Complex) other);
        } else if (other instanceof Int) {
            return this.plus(Complex.of(((Int) other).value()));
        } else if (other instanceof Decimal) {
            return this.plus(Complex.of(((Decimal) other).value()));
        }
        return Undefined.VALUE;
    }

    public Complex plus(Complex b) {
        return Complex.of(re + b.re, im + b.im);
    }

    @Override
    public Any minus(Any other) {
        if (other instanceof Complex) {
            return this.minus((Complex) other);
        } else if (other instanceof Int) {
            return this.minus(Complex.of(((Int) other).value()));
        } else if (other instanceof Decimal) {
            return this.minus(Complex.of(((Decimal) other).value()));
        }
        return Undefined.VALUE;
    }

    public Complex minus(Complex b) {
        return Complex.of(re - b.re, im - b.im);
    }

    @Override
    public Any times(Any other) {
        if (other instanceof Complex) {
            return this.times((Complex) other);
        } else if (other instanceof Int) {
            return this.times(Complex.of(((Int) other).value()));
        } else if (other instanceof Decimal) {
            return this.times(Complex.of(((Decimal) other).value()));
        }
        return Undefined.VALUE;
    }

    public Complex times(Complex b) {
        return Complex.of(re * b.re - im * b.im, re * b.im + im * b.re);
    }

    @Override
    public Any divide(Any other) {
        if (other instanceof Complex) {
            return this.divide((Complex) other);
        } else if (other instanceof Int) {
            return this.divide(Complex.of(((Int) other).value()));
        } else if (other instanceof Decimal) {
            return this.divide(Complex.of(((Decimal) other).value()));
        }
        return Undefined.VALUE;
    }

    @Override
    public Any pow(Any other) {
        if (other instanceof Complex) {
            return this.pow((Complex) other);
        } else if (other instanceof Int) {
            return this.pow(Complex.of(((Int) other).value()));
        } else if (other instanceof Decimal) {
            return this.pow(Complex.of(((Decimal) other).value()));
        }
        return Undefined.VALUE;
    }

    public Complex pow(Complex other) {
        Complex result = ln().times(other).exp();
        double real = BigDecimal.valueOf(result.re).setScale(7, BigDecimal.ROUND_HALF_EVEN).doubleValue();
        double imag = BigDecimal.valueOf(result.im).setScale(7, BigDecimal.ROUND_HALF_EVEN).doubleValue();
        return Complex.of(real, imag);
    }

    public Complex divide(Complex b) {
        return times(b.reciprocal());
    }

    private Complex reciprocal() {
        double scale = re * re + im * im;
        return Complex.of(re / scale, -im / scale);
    }
    
    public Complex sin() {
        double real = Math.sin(re) * Math.cosh(im);
        double imag = Math.cos(re) * Math.sinh(im);
        return Complex.of(real, imag);
    }

    public Complex cos() {
        double real = Math.cos(re) * Math.cosh(im);
        double imag = -Math.sin(re) * Math.sinh(im);
        return Complex.of(real, imag);
    }
    
    public Complex tan() {
        return sin().divide(cos());
    }

    public Complex sinh() {
        return new Complex(Math.sinh(re) * Math.cos(im), Math.cosh(re) * Math.sin(im));
    }

    public Complex cosh() {
        return new Complex(Math.cosh(re) * Math.cos(im), Math.sinh(re) * Math.sin(im));
    }

    public Complex tanh() {
        return sinh().divide(cosh());
    }

    private double abs() {
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

    private double arg() {
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
    public Truth isEqualTo(Any other) {
        if (other instanceof Complex) {
            return this.isEqualTo((Complex) other);
        } else if (other instanceof Int) {
            return this.isEqualTo(Complex.of(((Int) other).value()));
        } else if (other instanceof Decimal) {
            return this.isEqualTo(Complex.of(((Decimal) other).value()));
        }
        return Truth.FALSE;
    }

    private Truth isEqualTo(Complex b) {
        return re == b.re && im == b.im ? Truth.TRUE : Truth.FALSE;
    }

    public double real() {
        return re;
    }

    public double imaginary() {
        return im;
    }
}
