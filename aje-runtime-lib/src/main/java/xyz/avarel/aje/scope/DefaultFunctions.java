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

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.functions.Func;
import xyz.avarel.aje.runtime.functions.NativeFunc;
import xyz.avarel.aje.runtime.functions.Parameter;
import xyz.avarel.aje.runtime.numbers.Decimal;
import xyz.avarel.aje.runtime.numbers.Int;
import xyz.avarel.aje.runtime.numbers.Numeric;

import java.util.List;

public enum DefaultFunctions {
    SQUARE_ROOT(new NativeFunc(Numeric.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            double value = Numeric.convert(a, Decimal.TYPE).toJava();
            return Decimal.of(Math.sqrt(value));
        }
    }),
    CUBE_ROOT(new NativeFunc(Numeric.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(Math.cbrt(Numeric.convert(a, Decimal.TYPE).toJava()));
        }
    }),
    EXPONENTIAL(new NativeFunc(Numeric.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(Math.exp(Numeric.convert(a, Decimal.TYPE).toJava()));
        }
    }),
    LOG10(new NativeFunc(Numeric.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(Math.log10(Numeric.convert(a, Decimal.TYPE).toJava()));
        }
    }),
    LOG_NATURAL(new NativeFunc(Numeric.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(Math.log(Numeric.convert(a, Decimal.TYPE).toJava()));
        }
    }),
    ROUND(new NativeFunc(Numeric.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(Math.round(Numeric.convert(a, Decimal.TYPE).toJava()));
        }
    }),
    FLOOR(new NativeFunc(Numeric.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(Math.floor(Numeric.convert(a, Decimal.TYPE).toJava()));
        }
    }),
    CEILING(new NativeFunc(Numeric.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(Math.ceil(Numeric.convert(a, Decimal.TYPE).toJava()));
        }
    }),

    SUM(new NativeFunc(Parameter.of(Numeric.TYPE, true)) {
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

    PRODUCT(new NativeFunc(Parameter.of(Numeric.TYPE, true)) {
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

    SINE(new NativeFunc(Numeric.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(Math.sin(Numeric.convert(a, Decimal.TYPE).toJava()));
        }
    }),
    COSINE(new NativeFunc(Numeric.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(Math.cos(Numeric.convert(a, Decimal.TYPE).toJava()));
        }
    }),
    TANGENT(new NativeFunc(Numeric.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(Math.tan(Numeric.convert(a, Decimal.TYPE).toJava()));
        }
    }),
    COSECANT(new NativeFunc(Numeric.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(1).divide((Decimal) SINE.get().invoke(Numeric.convert(a, Decimal.TYPE)));
        }
    }),
    SECANT(new NativeFunc(Numeric.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(1).divide((Decimal) COSINE.get().invoke(Numeric.convert(a, Decimal.TYPE)));
        }
    }),
    COTANGENT(new NativeFunc(Numeric.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(1).divide((Decimal) TANGENT.get().invoke(Numeric.convert(a, Decimal.TYPE)));
        }
    }),
    HYPERBOLIC_SINE(new NativeFunc(Numeric.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(Math.sinh(Numeric.convert(a, Decimal.TYPE).toJava()));
        }
    }),
    HYPERBOLIC_COSINE(new NativeFunc(Numeric.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(Math.cosh(Numeric.convert(a, Decimal.TYPE).toJava()));
        }
    }),
    HYPERBOLIC_TANGENT(new NativeFunc(Numeric.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(Math.tanh(Numeric.convert(a, Decimal.TYPE).toJava()));
        }
    }),
    HYPERBOLIC_COSECANT(new NativeFunc(Numeric.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(1).divide((Decimal) HYPERBOLIC_SINE.get().invoke(Numeric.convert(a, Decimal.TYPE)));
        }
    }),
    HYPERBOLIC_SECANT(new NativeFunc(Numeric.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(1).divide((Decimal) HYPERBOLIC_COSINE.get().invoke(Numeric.convert(a, Decimal.TYPE)));
        }
    }),
    HYPERBOLIC_COTANGENT(new NativeFunc(Numeric.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(1).divide((Decimal) HYPERBOLIC_TANGENT.get().invoke(Numeric.convert(a, Decimal.TYPE)));
        }
    }),
    ARCSINE(new NativeFunc(Decimal.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(Math.asin(Numeric.convert(a, Decimal.TYPE).toJava()));
        }
    }),
    ARCCOSINE(new NativeFunc(Decimal.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(Math.acos(Numeric.convert(a, Decimal.TYPE).toJava()));
        }
    }),
    ARCTANGENT(new NativeFunc(Decimal.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return Decimal.of(Math.atan(Numeric.convert(a, Decimal.TYPE).toJava()));
        }
    }),
    ARCCOSECANT(new NativeFunc(Decimal.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return ARCSINE.get().invoke(Decimal.of(1).divide(Numeric.convert(a, Decimal.TYPE)));
        }
    }),
    ARCSECANT(new NativeFunc(Decimal.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return ARCCOSINE.get().invoke(Decimal.of(1).divide(Numeric.convert(a, Decimal.TYPE)));
        }
    }),
    ARCCOTANGENT(new NativeFunc(Decimal.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            return ARCTANGENT.get().invoke(Decimal.of(1).divide(Numeric.convert(a, Decimal.TYPE)));
        }
    }),
    ARCTANGENT2(new NativeFunc(Decimal.TYPE, Decimal.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            Obj a = arguments.get(0);
            Obj b = arguments.get(1);
            return Decimal.of(Math.atan2(
                    Numeric.convert(a, Decimal.TYPE).toJava(),
                    Numeric.convert(b, Decimal.TYPE).toJava()));
        }
    }),

    FACTORIAL(new NativeFunc(Decimal.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            int arg = Numeric.convert(arguments.get(0), Int.TYPE).toJava();
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
    }),

    NOT(new NativeFunc(Func.TYPE) {
        @Override
        protected Obj eval(List<Obj> arguments) {
            return new NativeFunc() {
                @Override
                protected Obj eval(List<Obj> arguments0) {
                    return arguments.get(0).invoke(arguments0).negate();
                }
            };
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
