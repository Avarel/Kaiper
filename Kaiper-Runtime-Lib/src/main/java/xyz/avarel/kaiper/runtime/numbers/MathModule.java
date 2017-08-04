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

package xyz.avarel.kaiper.runtime.numbers;

import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.functions.NativeFunc;
import xyz.avarel.kaiper.runtime.functions.Parameter;
import xyz.avarel.kaiper.runtime.modules.NativeModule;

import java.util.Map;

public class MathModule extends NativeModule {
    public static MathModule INSTANCE = new MathModule();

    private MathModule() {
        declare("PI", Number.of(Math.PI));
        declare("E", Number.of(Math.E));

        declare("sqrt", new NativeFunc("sqrt", "a") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Obj a = arguments.get(0);
                double value = a.as(Number.TYPE).toJava();
                return Number.of(Math.sqrt(value));
            }
        });
        declare("cbrt", new NativeFunc("cbrt", "a") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Obj a = arguments.get(0);
                return Number.of(Math.cbrt(a.as(Number.TYPE).toJava()));
            }
        });
        declare("exp", new NativeFunc("exp", "a") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Obj a = arguments.get(0);
                return Number.of(Math.exp(a.as(Number.TYPE).toJava()));
            }
        });
        declare("log", new NativeFunc("log", "a") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Obj a = arguments.get(0);
                return Number.of(Math.log10(a.as(Number.TYPE).toJava()));
            }
        });
        declare("ln", new NativeFunc("ln", "a") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Obj a = arguments.get(0);
                return Number.of(Math.log(a.as(Number.TYPE).toJava()));
            }
        });
        declare("round", new NativeFunc("round", "a") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Obj a = arguments.get(0);
                return Number.of(Math.round(a.as(Number.TYPE).toJava()));
            }
        });
        declare("floor", new NativeFunc("floor", "a") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Obj a = arguments.get(0);
                return Number.of(Math.floor(a.as(Number.TYPE).toJava()));
            }
        });
        declare("ceil", new NativeFunc("ceiling", "a") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Obj a = arguments.get(0);
                return Number.of(Math.ceil(a.as(Number.TYPE).toJava()));
            }
        });

        declare("sum", new NativeFunc("sum", Parameter.of("numbers...", true)) {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                if (arguments.isEmpty()) return Int.of(0);
                Obj accumulator = arguments.get(0);
                for (int i = 1; i < arguments.size(); i++) {
                    accumulator = accumulator.plus(arguments.get(i));
                }
                return accumulator;
            }
        });

        declare("product", new NativeFunc("product", Parameter.of("numbers...", true)) {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                if (arguments.isEmpty()) return Int.of(0);
                Obj accumulator = arguments.get(0);
                for (int i = 1; i < arguments.size(); i++) {
                    accumulator = accumulator.times(arguments.get(i));
                }
                return accumulator;
            }
        });

        declare("sin", new NativeFunc("sin", "a") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Obj a = arguments.get(0);
                return Number.of(Math.sin(a.as(Number.TYPE).toJava()));
            }
        });
        declare("cos", new NativeFunc("cos", "a") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Obj a = arguments.get(0);
                return Number.of(Math.cos(a.as(Number.TYPE).toJava()));
            }
        });
        declare("tan", new NativeFunc("tan", "a") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Obj a = arguments.get(0);
                return Number.of(Math.tan(a.as(Number.TYPE).toJava()));
            }
        });

        declare("sinh", new NativeFunc("sinh", "a") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Obj a = arguments.get(0);
                return Number.of(Math.sinh(a.as(Number.TYPE).toJava()));
            }
        });
        declare("cosh", new NativeFunc("cosh", "a") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Obj a = arguments.get(0);
                return Number.of(Math.cosh(a.as(Number.TYPE).toJava()));
            }
        });
        declare("tanh", new NativeFunc("tanh", "a") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Obj a = arguments.get(0);
                return Number.of(Math.tanh(a.as(Number.TYPE).toJava()));
            }
        });

        declare("asin", new NativeFunc("asin", "a") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Obj a = arguments.get(0);
                return Number.of(Math.asin(a.as(Number.TYPE).toJava()));
            }
        });
        declare("acos", new NativeFunc("acos", "a") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Obj a = arguments.get(0);
                return Number.of(Math.acos(a.as(Number.TYPE).toJava()));
            }
        });
        declare("atan", new NativeFunc("atan", "a") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Obj a = arguments.get(0);
                return Number.of(Math.atan(a.as(Number.TYPE).toJava()));
            }
        });
        declare("atan2", new NativeFunc("atan2", "y", "x") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Obj a = arguments.get(0);
                Obj b = arguments.get(1);
                return Number.of(Math.atan2(
                        a.as(Number.TYPE).toJava(),
                        ((Number) Number.TYPE.invoke(b)).toJava()));
            }
        });

        declare("factorial", new NativeFunc("factorial", "integer") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                int arg = arguments.get(0).as(Int.TYPE).toJava();
                int result = arg;

                for (int i = arg - 1; i > 0; i--) {
                    result *= i;
                }

                return Number.of(result);
            }
        });
        declare("random", new NativeFunc("random") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                return Number.of(Math.random());
            }
        });
    }
}
