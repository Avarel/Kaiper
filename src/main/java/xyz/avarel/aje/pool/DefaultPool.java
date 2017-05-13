package xyz.avarel.aje.pool;

import xyz.avarel.aje.functional.AJEFunction;
import xyz.avarel.aje.functional.NativeFunction;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.numbers.Complex;
import xyz.avarel.aje.types.numbers.Decimal;
import xyz.avarel.aje.types.numbers.Int;
import xyz.avarel.aje.types.numbers.Numeric;
import xyz.avarel.aje.types.others.Slice;
import xyz.avarel.aje.types.others.Truth;
import xyz.avarel.aje.types.others.Undefined;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultPool {
    private static Map<String, Any> pool = new HashMap<>();
    
    static {
        pool.put("pi", Decimal.of(Math.PI));
        pool.put("e", Decimal.of(Math.E));

        pool.put("sin", new NativeFunction(Numeric.TYPE) {
            @Override
            public Any eval(List<Any> arguments) {
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
        });
        pool.put("cos", new NativeFunction(Numeric.TYPE) {
            @Override
            public Any eval(List<Any> arguments) {
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
        });
        pool.put("tan", new NativeFunction(Numeric.TYPE) {
            @Override
            public Any eval(List<Any> arguments) {
                Any a = arguments.get(0);
                if (a instanceof Int || a instanceof Decimal) {
                    return Decimal.of(Math.tan(Numeric.convert(a, Decimal.TYPE).toNative()));
                } else if (a instanceof Complex) {
                    double re1 = Math.sin(((Complex) a).real()) * Math.cosh(((Complex) a).imaginary());
                    double im1 = Math.cos(((Complex) a).real()) * Math.sinh(((Complex) a).imaginary());
                    Complex sin = Complex.of(re1, im1);

                    double re2 = Math.cos(((Complex) a).real()) * Math.cosh(((Complex) a).imaginary());
                    double im2 = -Math.sin(((Complex) a).real()) * Math.sinh(((Complex) a).imaginary());
                    Complex cos = Complex.of(re2, im2);

                    return sin.divide(cos);
                }
                return Undefined.VALUE;
            }
        });
        pool.put("asin", new NativeFunction(Numeric.TYPE) {
            @Override
            public Any eval(List<Any> arguments) {
                Any a = arguments.get(0);
                return Decimal.of(Math.asin(Numeric.convert(a, Decimal.TYPE).toNative()));
            }
        });
        pool.put("acos", new NativeFunction(Numeric.TYPE) {
            @Override
            public Any eval(List<Any> arguments) {
                Any a = arguments.get(0);
                return Decimal.of(Math.acos(Numeric.convert(a, Decimal.TYPE).toNative()));
            }
        });
        pool.put("atan", new NativeFunction(Numeric.TYPE) {
            @Override
            public Any eval(List<Any> arguments) {
                Any a = arguments.get(0);
                return Decimal.of(Math.atan(Numeric.convert(a, Decimal.TYPE).toNative()));
            }
        });
        pool.put("atan2", new NativeFunction(Numeric.TYPE, Numeric.TYPE) {
            @Override
            public Any eval(List<Any> arguments) {
                Any a = arguments.get(0);
                Any b = arguments.get(1);
                return Decimal.of(Math.atan2(
                        Numeric.convert(a, Decimal.TYPE).toNative(),
                        Numeric.convert(b, Decimal.TYPE).toNative()));
            }
        });
        pool.put("sqrt", new NativeFunction(Numeric.TYPE) {
            @Override
            public Any eval(List<Any> arguments) {
                Any arg = arguments.get(0);
                if (arg instanceof Int || arg instanceof Decimal) {
                    return Decimal.of(Math.cos(Numeric.convert(arg, Decimal.TYPE).toNative()));
                } else if (arg instanceof Complex) {
                    return ((Complex) arg).pow(new Complex(1.0/2.0, 0));
                }
                return Undefined.VALUE;
            }
        });
        pool.put("cbrt", new NativeFunction(Numeric.TYPE) {
            @Override
            public Any eval(List<Any> arguments) {
                Any arg = arguments.get(0);
                if (arg instanceof Int || arg instanceof Decimal) {
                    return Decimal.of(Math.cbrt(Numeric.convert(arg, Decimal.TYPE).toNative()));
                } else if (arg instanceof Complex) {
                    return ((Complex) arg).pow(new Complex(1.0/3.0, 0));
                }
                return Undefined.VALUE;
            }
        });
        pool.put("map", new NativeFunction(Slice.TYPE, AJEFunction.TYPE) {
            @Override
            public Any eval(List<Any> arguments) {
                Slice arg = (Slice) arguments.get(0);
                AJEFunction transform = (AJEFunction) arguments.get(1);

                Slice slice = new Slice();
                for (Any obj : arg) {
                    slice.add(transform.invoke(Collections.singletonList(obj)));
                }
                return slice;
            }
        });

        // Types
        pool.put("Int", Int.TYPE);
        pool.put("Decimal", Decimal.TYPE);
        pool.put("Complex", Complex.TYPE);
        pool.put("Boolean", Truth.TYPE);
        pool.put("Slice", Slice.TYPE);
    }

    public static Map<String, Any> copy() {
        return new HashMap<>(pool);
    }
}
