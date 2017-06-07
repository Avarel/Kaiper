/*
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package xyz.avarel.aje.runtime.numbers;

import xyz.avarel.aje.runtime.Bool;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Type;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.functions.NativeFunc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class Complex implements Obj<Double> {
    public static final Type<Complex> TYPE = new ComplexType();

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
    public Double toJava() {
        return re;
    }

    @Override
    public Type<Complex> getType() {
        return TYPE;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Complex) {
            Complex b = (Complex) obj;
            return re == b.re && im == b.im;
        } else if (obj instanceof Obj) {
            return isEqualTo((Obj) obj) == Bool.TRUE;
        }
        return false;
    }

    @Override
    public String toString() {
        if (im == 0) return String.valueOf(re);
        if (re == 0 && im == 1) return "i";
        if (re == 0) return im + "i";
        if (im < 0) return re + " - " + (-im) + "i";
        return re + " + " + im + "i";
    }

    @Override
    public int hashCode() {
        return Objects.hash(Double.hashCode(re), Double.hashCode(im));
    }

    @Override
    public Obj plus(Obj other) {
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
    public Obj minus(Obj other) {
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
    public Obj times(Obj other) {
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
    public Obj divide(Obj other) {
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
    public Obj pow(Obj other) {
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


    @Override
    public Bool isEqualTo(Obj other) {
        if (other instanceof Complex) {
            return this.isEqualTo((Complex) other);
        } else if (other instanceof Int) {
            return this.isEqualTo(Complex.of(((Int) other).value()));
        } else if (other instanceof Decimal) {
            return this.isEqualTo(Complex.of(((Decimal) other).value()));
        }
        return Bool.FALSE;
    }

    private Bool isEqualTo(Complex b) {
        return Bool.of(equals(b));
    }

    @Override
    public Obj greaterThan(Obj other) {
        if (other instanceof Complex) {
            return this.greaterThan((Complex) other);
        } else if (other instanceof Int) {
            return this.greaterThan(Complex.of(((Int) other).value()));
        } else if (other instanceof Decimal) {
            return this.greaterThan(Complex.of(((Decimal) other).value()));
        }
        return Bool.FALSE;
    }

    private Obj greaterThan(Complex other) {
        return Bool.of(abs() > other.abs());
    }

    @Override
    public Obj lessThan(Obj other) {
        if (other instanceof Complex) {
            return this.lessThan((Complex) other);
        } else if (other instanceof Int) {
            return this.lessThan(Complex.of(((Int) other).value()));
        } else if (other instanceof Decimal) {
            return this.lessThan(Complex.of(((Decimal) other).value()));
        }
        return Bool.FALSE;
    }

    private Bool lessThan(Complex other) {
        return Bool.of(abs() < other.abs());
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

    private double abs() { // distance from origin
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

    public double real() {
        return re;
    }

    public double imaginary() {
        return im;
    }

    @Override
    public Obj invoke(List<Obj> arguments) {
        if (arguments.size() == 1) {
            return times(arguments.get(0));
        }
        return Undefined.VALUE;
    }

    private static class ComplexType extends Type<Complex> {
        public ComplexType() {
            super(Numeric.TYPE, "Complex");

            getScope().declare("toInt", new NativeFunc(this) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return Int.of((int) ((Complex) arguments.get(0)).re);
                }
            });
            getScope().declare("toDecimal", new NativeFunc(this) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return Decimal.of(((Complex) arguments.get(0)).re);
                }
            });
            getScope().declare("toComplex", new NativeFunc(this) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return arguments.get(0);
                }
            });
        }
    }
}
