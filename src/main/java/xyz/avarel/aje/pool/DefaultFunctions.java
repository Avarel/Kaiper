package xyz.avarel.aje.pool;

import xyz.avarel.aje.functional.AJEFunction;
import xyz.avarel.aje.functional.NativeFunction;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.Slice;
import xyz.avarel.aje.types.Truth;
import xyz.avarel.aje.types.Undefined;
import xyz.avarel.aje.types.numbers.Complex;
import xyz.avarel.aje.types.numbers.Decimal;
import xyz.avarel.aje.types.numbers.Int;
import xyz.avarel.aje.types.numbers.Numeric;

import java.util.Collections;
import java.util.List;

public enum DefaultFunctions {
    SQUARE_ROOT(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any arg = arguments.get(0);
            if (arg instanceof Int || arg instanceof Decimal) {
                double value = Numeric.convert(arg, Decimal.TYPE).toNative();

                if (value < 0) {
                    return this.invoke(Complex.of(value));
                }

                return Decimal.of(Math.sqrt(value));
            } else if (arg instanceof Complex) {
                return ((Complex) arg).pow(Complex.of(0.5, 0));
            }
            return Undefined.VALUE;
        }
    }),
    CUBE_ROOT(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any arg = arguments.get(0);
            if (arg instanceof Int || arg instanceof Decimal) {
                return Decimal.of(Math.cbrt(Numeric.convert(arg, Decimal.TYPE).toNative()));
            } else if (arg instanceof Complex) {
                return ((Complex) arg).pow(Complex.of(1.0/3.0, 0));
            }
            return Undefined.VALUE;
        }
    }),
    EXPONENTIAL(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any arg = arguments.get(0);
            if (arg instanceof Int || arg instanceof Decimal) {
                return Decimal.of(Math.exp(Numeric.convert(arg, Decimal.TYPE).toNative()));
            } else if (arg instanceof Complex) {
                return ((Complex) arg).exp();
            }
            return Undefined.VALUE;
        }
    }),
    LOG10(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any arg = arguments.get(0);
            if (arg instanceof Int || arg instanceof Decimal) {
                return Decimal.of(Math.log10(Numeric.convert(arg, Decimal.TYPE).toNative()));
            } else if (arg instanceof Complex) {
                return ((Complex) arg).ln().divide(Complex.of(10).ln());
            }
            return Undefined.VALUE;
        }
    }),
    LOG_NATURAL(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any arg = arguments.get(0);
            if (arg instanceof Int || arg instanceof Decimal) {
                return Decimal.of(Math.log(Numeric.convert(arg, Decimal.TYPE).toNative()));
            } else if (arg instanceof Complex) {
                return ((Complex) arg).ln();
            }
            return Undefined.VALUE;
        }
    }),
    ROUND(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any arg = arguments.get(0);
            if (arg instanceof Int || arg instanceof Decimal) {
                return Decimal.of(Math.round(Numeric.convert(arg, Decimal.TYPE).toNative()));
            } else if (arg instanceof Complex) {
                return ((Complex) arg).round();
            }
            return Undefined.VALUE;
        }
    }),
    FLOOR(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any arg = arguments.get(0);
            if (arg instanceof Int || arg instanceof Decimal) {
                return Decimal.of(Math.floor(Numeric.convert(arg, Decimal.TYPE).toNative()));
            } else if (arg instanceof Complex) {
                return ((Complex) arg).floor();
            }
            return Undefined.VALUE;
        }
    }),
    CEILING(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any arg = arguments.get(0);
            if (arg instanceof Int || arg instanceof Decimal) {
                return Decimal.of(Math.ceil(Numeric.convert(arg, Decimal.TYPE).toNative()));
            } else if (arg instanceof Complex) {
                return ((Complex) arg).ceil();
            }
            return Undefined.VALUE;
        }
    }),

    SINE(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(Math.sin(Numeric.convert(a, Decimal.TYPE).toNative()));
            } else if (a instanceof Complex) {
                double re = Math.sin(((Complex) a).real()) * Math.cosh(((Complex) a).imaginary());
                double im = Math.cos(((Complex) a).real()) * Math.sinh(((Complex) a).imaginary());
                return Complex.of(re, im);
            }
            return Undefined.VALUE;
        }
    }),
    COSINE(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(Math.cos(Numeric.convert(a, Decimal.TYPE).toNative()));
            } else if (a instanceof Complex) {
                double re = Math.cos(((Complex) a).real()) * Math.cosh(((Complex) a).imaginary());
                double im = -Math.sin(((Complex) a).real()) * Math.sinh(((Complex) a).imaginary());
                return Complex.of(re, im);
            }
            return Undefined.VALUE;
        }
    }),
    TANGENT(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(Math.tan(Numeric.convert(a, Decimal.TYPE).toNative()));
            } else if (a instanceof Complex) {
                Complex sin = (Complex) SINE.getFunction().invoke(a);
                Complex cos = (Complex) COSINE.getFunction().invoke(a);

                return sin.divide(cos);
            }
            return Undefined.VALUE;
        }
    }),
    HYPERBOLIC_SINE(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(Math.sinh(Numeric.convert(a, Decimal.TYPE).toNative()));
            } else if (a instanceof Complex) {
                return ePlus((Complex) a).divide(Complex.of(2));
            }
            return Undefined.VALUE;
        }
    }),
    HYPERBOLIC_COSINE(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(Math.cosh(Numeric.convert(a, Decimal.TYPE).toNative()));
            } else if (a instanceof Complex) {
                return eMinus((Complex) a).divide(Complex.of(2));
            }
            return Undefined.VALUE;
        }
    }),
    HYPERBOLIC_TANGENT(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(Math.tanh(Numeric.convert(a, Decimal.TYPE).toNative()));
            } else if (a instanceof Complex) {
                return ePlus((Complex) a).divide(eMinus((Complex) a));
            }
            return Undefined.VALUE;
        }
    }),
    SECANT(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(1).divide((Decimal) COSINE.getFunction().invoke(Numeric.convert(a, Decimal.TYPE)));
            } else if (a instanceof Complex) {
                return Complex.of(1).divide((Complex) COSINE.getFunction().invoke(Numeric.convert(a, Complex.TYPE)));
            }
            return Undefined.VALUE;
        }
    }),
    COSECANT(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(1).divide((Decimal) SINE.getFunction().invoke(Numeric.convert(a, Decimal.TYPE)));
            } else if (a instanceof Complex) {
                return Complex.of(1).divide((Complex) SINE.getFunction().invoke(Numeric.convert(a, Complex.TYPE)));
            }
            return Undefined.VALUE;
        }
    }),
    COTANGENT(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(1).divide((Decimal) TANGENT.getFunction().invoke(Numeric.convert(a, Decimal.TYPE)));
            } else if (a instanceof Complex) {
                return Complex.of(1).divide((Complex) TANGENT.getFunction().invoke(Numeric.convert(a, Complex.TYPE)));
            }
            return Undefined.VALUE;
        }
    }),
    ARCSINE(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any a = arguments.get(0);
            return Decimal.of(Math.asin(Numeric.convert(a, Decimal.TYPE).toNative()));
        }
    }),
    ARCCOSINE(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any a = arguments.get(0);
            return Decimal.of(Math.acos(Numeric.convert(a, Decimal.TYPE).toNative()));
        }
    }),
    ARCTANGENT(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any a = arguments.get(0);
            return Decimal.of(Math.atan(Numeric.convert(a, Decimal.TYPE).toNative()));
        }
    }),
    ARCSECANT(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any a = arguments.get(0);
            return ARCCOSINE.getFunction().invoke(Decimal.of(1).divide(Numeric.convert(a, Decimal.TYPE)));
        }
    }),
    ARCCOSECANT(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any a = arguments.get(0);
            return ARCSINE.getFunction().invoke(Decimal.of(1).divide(Numeric.convert(a, Decimal.TYPE)));
        }
    }),
    ARCCOTANGENT(new NativeFunction(Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any a = arguments.get(0);
            return ARCTANGENT.getFunction().invoke(Decimal.of(1).divide(Numeric.convert(a, Decimal.TYPE)));
        }
    }),
    ARCTANGENT2(new NativeFunction(Numeric.TYPE, Numeric.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Any a = arguments.get(0);
            Any b = arguments.get(1);
            return Decimal.of(Math.atan2(
                    Numeric.convert(a, Decimal.TYPE).toNative(),
                    Numeric.convert(b, Decimal.TYPE).toNative()));
        }
    }),

    MAP(new NativeFunction(Slice.TYPE, AJEFunction.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Slice arg = (Slice) arguments.get(0);
            AJEFunction transform = (AJEFunction) arguments.get(1);

            Slice slice = new Slice();
            for (Any obj : arg) {
                slice.add(transform.invoke(Collections.singletonList(obj)));
            }
            return slice;
        }
    }),
    FILTER(new NativeFunction(Slice.TYPE, AJEFunction.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Slice arg = (Slice) arguments.get(0);
            AJEFunction predicate = (AJEFunction) arguments.get(1);

            Slice slice = new Slice();
            for (Any obj : arg) {
                Truth condition = (Truth) predicate.invoke(Collections.singletonList(obj));
                if (condition == Truth.TRUE) slice.add(obj);
            }
            return slice;
        }
    }),
    FOLD(new NativeFunction(Slice.TYPE, Any.TYPE, AJEFunction.TYPE) {
        @Override
        protected Any eval(List<Any> arguments) {
            Slice arg = (Slice) arguments.get(0);
            Any accumulator = arguments.get(1);
            AJEFunction operation = (AJEFunction) arguments.get(2);

            for (Any obj : arg) {
                accumulator = operation.invoke(accumulator, obj);
            }
            return accumulator;
        }
    }),
    ;
    private final NativeFunction function;

    DefaultFunctions(NativeFunction function) {
        this.function = function;
    }

    public NativeFunction getFunction() {
        return function;
    }

    // UTILITIES
    private static Complex eMinus(Complex arg) { // e^x + e-x
        return Complex.of(Math.E).pow(arg).plus(Complex.of(Math.E).pow(arg.negative()));
    }

    private static Complex ePlus(Complex arg) { // e^x + e^-x
        return Complex.of(Math.E).pow(arg).minus(Complex.of(Math.E).pow(arg.negative()));
    }
}
