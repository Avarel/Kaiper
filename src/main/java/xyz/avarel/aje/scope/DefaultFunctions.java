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

package xyz.avarel.aje.scope;

import xyz.avarel.aje.runtime.Bool;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.collections.Vector;
import xyz.avarel.aje.runtime.functions.ComposedFunc;
import xyz.avarel.aje.runtime.functions.Func;
import xyz.avarel.aje.runtime.functions.NativeFunc;
import xyz.avarel.aje.runtime.numbers.Complex;
import xyz.avarel.aje.runtime.numbers.Decimal;
import xyz.avarel.aje.runtime.numbers.Int;
import xyz.avarel.aje.runtime.numbers.Numeric;

import java.util.Collections;
import java.util.List;

public enum DefaultFunctions {
    SQUARE_ROOT(new NativeFunc(Numeric.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                double value = Numeric.convert(a, Decimal.CLS).toJava();

                if (value < 0) {
                    return this.invoke(Complex.of(value));
                }

                return Decimal.of(Math.sqrt(value));
            } else if (a instanceof Complex) {
                return ((Complex) a).pow(Complex.of(0.5, 0));
            }
            return Undefined.VALUE;
        }
    }),
    CUBE_ROOT(new NativeFunc(Numeric.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(Math.cbrt(Numeric.convert(a, Decimal.CLS).toJava()));
            } else if (a instanceof Complex) {
                return ((Complex) a).pow(Complex.of(0.3333333333333333, 0));
            }
            return Undefined.VALUE;
        }
    }),
    EXPONENTIAL(new NativeFunc(Numeric.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(Math.exp(Numeric.convert(a, Decimal.CLS).toJava()));
            } else if (a instanceof Complex) {
                return ((Complex) a).exp();
            }
            return Undefined.VALUE;
        }
    }),
    LOG10(new NativeFunc(Numeric.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(Math.log10(Numeric.convert(a, Decimal.CLS).toJava()));
            } else if (a instanceof Complex) {
                return ((Complex) a).ln().divide(Complex.of(10).ln());
            }
            return Undefined.VALUE;
        }
    }),
    LOG_NATURAL(new NativeFunc(Numeric.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(Math.log(Numeric.convert(a, Decimal.CLS).toJava()));
            } else if (a instanceof Complex) {
                return ((Complex) a).ln();
            }
            return Undefined.VALUE;
        }
    }),
    ROUND(new NativeFunc(Numeric.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(Math.round(Numeric.convert(a, Decimal.CLS).toJava()));
            } else if (a instanceof Complex) {
                return ((Complex) a).round();
            }
            return Undefined.VALUE;
        }
    }),
    FLOOR(new NativeFunc(Numeric.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(Math.floor(Numeric.convert(a, Decimal.CLS).toJava()));
            } else if (a instanceof Complex) {
                return ((Complex) a).floor();
            }
            return Undefined.VALUE;
        }
    }),
    CEILING(new NativeFunc(Numeric.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(Math.ceil(Numeric.convert(a, Decimal.CLS).toJava()));
            } else if (a instanceof Complex) {
                return ((Complex) a).ceil();
            }
            return Undefined.VALUE;
        }
    }),

    SUM(new NativeFunc(true, Numeric.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            if (arguments.isEmpty()) return Int.of(0);
            Obj accumulator = arguments.get(0);
            for (int i = 1; i < arguments.size(); i++) {
                accumulator = accumulator.plus(arguments.get(i));
            }
            return accumulator;
        }
    }),

    PRODUCT(new NativeFunc(true, Numeric.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            if (arguments.isEmpty()) return Int.of(0);
            Obj accumulator = arguments.get(0);
            for (int i = 1; i < arguments.size(); i++) {
                accumulator = accumulator.times(arguments.get(i));
            }
            return accumulator;
        }
    }),

    COMPOSE(new NativeFunc(Func.CLS, Func.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            return new ComposedFunc((Func) arguments.get(0), (Func) arguments.get(1));
        }
    }),

    SINE(new NativeFunc(Numeric.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(Math.sin(Numeric.convert(a, Decimal.CLS).toJava()));
            } else if (a instanceof Complex) {
                return ((Complex) a).sin();
            }
            return Undefined.VALUE;
        }
    }),
    COSINE(new NativeFunc(Numeric.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(Math.cos(Numeric.convert(a, Decimal.CLS).toJava()));
            } else if (a instanceof Complex) {
                return ((Complex) a).cos();
            }
            return Undefined.VALUE;
        }
    }),
    TANGENT(new NativeFunc(Numeric.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(Math.tan(Numeric.convert(a, Decimal.CLS).toJava()));
            } else if (a instanceof Complex) {
                return ((Complex) a).tan();
            }
            return Undefined.VALUE;
        }
    }),
    COSECANT(new NativeFunc(Numeric.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(1).divide((Decimal) SINE.get().invoke(Numeric.convert(a, Decimal.CLS)));
            } else if (a instanceof Complex) {
                return Complex.of(1).divide((Complex) SINE.get().invoke(Numeric.convert(a, Complex.CLS)));
            }
            return Undefined.VALUE;
        }
    }),
    SECANT(new NativeFunc(Numeric.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(1).divide((Decimal) COSINE.get().invoke(Numeric.convert(a, Decimal.CLS)));
            } else if (a instanceof Complex) {
                return Complex.of(1).divide((Complex) COSINE.get().invoke(Numeric.convert(a, Complex.CLS)));
            }
            return Undefined.VALUE;
        }
    }),
    COTANGENT(new NativeFunc(Numeric.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(1).divide((Decimal) TANGENT.get().invoke(Numeric.convert(a, Decimal.CLS)));
            } else if (a instanceof Complex) {
                return Complex.of(1).divide((Complex) TANGENT.get().invoke(Numeric.convert(a, Complex.CLS)));
            }
            return Undefined.VALUE;
        }
    }),
    HYPERBOLIC_SINE(new NativeFunc(Numeric.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(Math.sinh(Numeric.convert(a, Decimal.CLS).toJava()));
            } else if (a instanceof Complex) {
                return ((Complex) a).sinh();
            }
            return Undefined.VALUE;
        }
    }),
    HYPERBOLIC_COSINE(new NativeFunc(Numeric.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(Math.cosh(Numeric.convert(a, Decimal.CLS).toJava()));
            } else if (a instanceof Complex) {
                return ((Complex) a).cosh();
            }
            return Undefined.VALUE;
        }
    }),
    HYPERBOLIC_TANGENT(new NativeFunc(Numeric.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(Math.tanh(Numeric.convert(a, Decimal.CLS).toJava()));
            } else if (a instanceof Complex) {
                return ((Complex) a).tanh();
            }
            return Undefined.VALUE;
        }
    }),
    HYPERBOLIC_COSECANT(new NativeFunc(Numeric.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(1).divide((Decimal) HYPERBOLIC_SINE.get().invoke(Numeric.convert(a, Decimal.CLS)));
            } else if (a instanceof Complex) {
                return Complex.of(1).divide((Complex) HYPERBOLIC_SINE.get().invoke(Numeric.convert(a, Complex.CLS)));
            }
            return Undefined.VALUE;
        }
    }),
    HYPERBOLIC_SECANT(new NativeFunc(Numeric.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(1).divide((Decimal) HYPERBOLIC_COSINE.get().invoke(Numeric.convert(a, Decimal.CLS)));
            } else if (a instanceof Complex) {
                return Complex.of(1).divide((Complex) HYPERBOLIC_COSINE.get().invoke(Numeric.convert(a, Complex.CLS)));
            }
            return Undefined.VALUE;
        }
    }),
    HYPERBOLIC_COTANGENT(new NativeFunc(Numeric.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            if (a instanceof Int || a instanceof Decimal) {
                return Decimal.of(1)
                        .divide((Decimal) HYPERBOLIC_TANGENT.get().invoke(Numeric.convert(a, Decimal.CLS)));
            } else if (a instanceof Complex) {
                return HYPERBOLIC_COSINE.get().invoke(a).divide(HYPERBOLIC_SINE.get().invoke(a));
            }
            return Undefined.VALUE;
        }
    }),
    ARCSINE(new NativeFunc(Decimal.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(Math.asin(Numeric.convert(a, Decimal.CLS).toJava()));
        }
    }),
    ARCCOSINE(new NativeFunc(Decimal.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(Math.acos(Numeric.convert(a, Decimal.CLS).toJava()));
        }
    }),
    ARCTANGENT(new NativeFunc(Decimal.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(Math.atan(Numeric.convert(a, Decimal.CLS).toJava()));
        }
    }),
    ARCCOSECANT(new NativeFunc(Decimal.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return ARCSINE.get().invoke(Decimal.of(1).divide(Numeric.convert(a, Decimal.CLS)));
        }
    }),
    ARCSECANT(new NativeFunc(Decimal.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return ARCCOSINE.get().invoke(Decimal.of(1).divide(Numeric.convert(a, Decimal.CLS)));
        }
    }),
    ARCCOTANGENT(new NativeFunc(Decimal.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return ARCTANGENT.get().invoke(Decimal.of(1).divide(Numeric.convert(a, Decimal.CLS)));
        }
    }),
    ARCTANGENT2(new NativeFunc(Decimal.CLS, Decimal.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            Obj b = arguments.get(1);
            return Decimal.of(Math.atan2(
                    Numeric.convert(a, Decimal.CLS).toJava(),
                    Numeric.convert(b, Decimal.CLS).toJava()));
        }
    }),

    FOREACH(new NativeFunc(Vector.CLS, Func.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Vector arg = (Vector) arguments.get(0);
            Func action = (Func) arguments.get(1);

            for (Obj obj : arg) {
                action.invoke(Collections.singletonList(obj));
            }
            return Undefined.VALUE;
        }
    }),
    MAP(new NativeFunc(Vector.CLS, Func.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Vector arg = (Vector) arguments.get(0);
            Func transform = (Func) arguments.get(1);

            Vector vector = new Vector();
            for (Obj obj : arg) {
                vector.add(transform.invoke(Collections.singletonList(obj)));
            }
            return vector;
        }
    }),
    FILTER(new NativeFunc(Vector.CLS, Func.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Vector arg = (Vector) arguments.get(0);
            Func predicate = (Func) arguments.get(1);

            Vector vector = new Vector();
            for (Obj obj : arg) {
                Bool condition = (Bool) predicate.invoke(Collections.singletonList(obj));
                if (condition == Bool.TRUE) vector.add(obj);
            }
            return vector;
        }
    }),
    FOLD(new NativeFunc(Vector.CLS, Obj.CLS, Func.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Vector arg = (Vector) arguments.get(0);
            Obj accumulator = arguments.get(1);
            Func operation = (Func) arguments.get(2);

            for (Obj obj : arg) {
                accumulator = operation.invoke(accumulator, obj);
            }
            return accumulator;
        }
    }),
    FACTORIAL(new NativeFunc(Decimal.CLS) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            int arg = Numeric.convert(arguments.get(0), Int.CLS).toJava();
            int result = arg;

            for(int i = arg - 1; i > 0; i--) {
                result *= i;
            }

            return Decimal.of(result);
        }
    }),
    RANDOM(new NativeFunc() {
        @Override
        protected Obj eval(List<Obj> arguments) {
            return Decimal.of(Math.random());
        }
    });

    private final NativeFunc function;

    DefaultFunctions(NativeFunc function) {
        this.function = function;
    }

    public NativeFunc get() {
        return function;
    }
}
