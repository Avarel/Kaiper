/*
 *  Copyright 2017 An Tran and Adrian Todt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package xyz.avarel.kaiper.lib.modules;

import xyz.avarel.kaiper.ast.pattern.PatternCase;
import xyz.avarel.kaiper.ast.pattern.RestPattern;
import xyz.avarel.kaiper.interpreter.ExprInterpreter;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.collections.Array;
import xyz.avarel.kaiper.runtime.functions.JavaFunction;
import xyz.avarel.kaiper.runtime.modules.NativeModule;
import xyz.avarel.kaiper.runtime.numbers.Int;
import xyz.avarel.kaiper.runtime.numbers.Number;

public class MathModule extends NativeModule {
    public MathModule(ExprInterpreter interpreter) {
        super("Math");

        declare("PI", Number.of(Math.PI));
        declare("E", Number.of(Math.E));

        declare("sqrt", new JavaFunction("sqrt", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    Obj a = scope.get("a");
                    double value = a.as(Number.TYPE).toJava();
                    return Number.of(Math.sqrt(value)); 
                }));
        declare("cbrt", new JavaFunction("cbrt", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    Obj a = scope.get("a");
                    return Number.of(Math.cbrt(a.as(Number.TYPE).toJava()));
                }));
        declare("exp", new JavaFunction("exp", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    Obj a = scope.get("a");
                    return Number.of(Math.exp(a.as(Number.TYPE).toJava()));
                }));
        declare("log", new JavaFunction("log", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    Obj a = scope.get("a");
                    return Number.of(Math.log10(a.as(Number.TYPE).toJava()));
                }));
        declare("ln", new JavaFunction("ln", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    Obj a = scope.get("a");
                    return Number.of(Math.log(a.as(Number.TYPE).toJava()));
                }));
        declare("round", new JavaFunction("round", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    Obj a = scope.get("a");
                    return Number.of(Math.round(a.as(Number.TYPE).toJava()));
                }));
        declare("floor", new JavaFunction("floor", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    Obj a = scope.get("a");
                    return Number.of(Math.floor(a.as(Number.TYPE).toJava()));
                }));
        declare("ceil", new JavaFunction("ceil", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    Obj a = scope.get("a");
                    return Number.of(Math.ceil(a.as(Number.TYPE).toJava()));
                }));

        declare("sum", new JavaFunction("sum", interpreter)
                .addDispatch(new PatternCase(new RestPattern("a")), scope -> {
                    Array numbers = scope.get("a").as(Array.TYPE);
                    if (numbers.isEmpty()) return Int.of(0);

                    Obj accumulator = numbers.get(0);
                    for (int i = 1; i < numbers.size(); i++) {
                        accumulator = accumulator.plus(numbers.get(i));
                    }
                    return accumulator;
                }));

        declare("product", new JavaFunction("sqrt", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    Array numbers = scope.get("a").as(Array.TYPE);
                    if (numbers.isEmpty()) return Int.of(0);

                    Obj accumulator = numbers.get(0);
                    for (int i = 1; i < numbers.size(); i++) {
                        accumulator = accumulator.times(numbers.get(i));
                    }
                    return accumulator;
                }));

        declare("sin", new JavaFunction("sin", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    Obj a = scope.get("a");
                    return Number.of(Math.sin(a.as(Number.TYPE).toJava()));
                }));
        declare("cos", new JavaFunction("cos", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    Obj a = scope.get("a");
                    return Number.of(Math.cos(a.as(Number.TYPE).toJava()));
                }));
        declare("tan", new JavaFunction("tan", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    Obj a = scope.get("a");
                    return Number.of(Math.tan(a.as(Number.TYPE).toJava()));
                }));

        declare("sinh", new JavaFunction("sinh", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    Obj a = scope.get("a");
                    return Number.of(Math.sinh(a.as(Number.TYPE).toJava()));
                }));
        declare("cosh", new JavaFunction("cosh", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    Obj a = scope.get("a");
                    return Number.of(Math.cosh(a.as(Number.TYPE).toJava()));
                }));
        declare("tanh", new JavaFunction("tanh", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    Obj a = scope.get("a");
                    return Number.of(Math.tanh(a.as(Number.TYPE).toJava()));
                }));

        declare("asin", new JavaFunction("asin", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    Obj a = scope.get("a");
                    return Number.of(Math.asin(a.as(Number.TYPE).toJava()));
                }));
        declare("acos", new JavaFunction("acos", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    Obj a = scope.get("a");
                    return Number.of(Math.acos(a.as(Number.TYPE).toJava()));
                }));
        declare("atan", new JavaFunction("atan", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    Obj a = scope.get("a");
                    return Number.of(Math.atan(a.as(Number.TYPE).toJava()));
                }));
        declare("atan2", new JavaFunction("atan2", interpreter)
                .addDispatch(new PatternCase("y", "x"), scope -> {
                    double y = scope.get("y").as(Number.TYPE).toJava();
                    double x = scope.get("x").as(Number.TYPE).toJava();
                    return Number.of(Math.atan2(y, x));
                }));

        declare("factorial", new JavaFunction("factorial", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    int arg = scope.get("a").as(Int.TYPE).toJava();
                    int result = arg;

                    for (int i = arg - 1; i > 0; i--) {
                        result *= i;
                    }

                    return Number.of(result);
                }));
        declare("random", new JavaFunction("random", interpreter)
                .addDispatch(PatternCase.EMPTY, scope -> Number.of(Math.random())));
    }
}
