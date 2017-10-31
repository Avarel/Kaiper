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
import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.interpreter.ExprInterpreter;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.functions.JavaFunction;
import xyz.avarel.kaiper.runtime.modules.NativeModule;
import xyz.avarel.kaiper.runtime.numbers.Int;
import xyz.avarel.kaiper.runtime.numbers.Number;

public class NumberModule extends NativeModule {
    public NumberModule(ExprInterpreter interpreter) {
        super("Number");
        declare("TYPE", Number.TYPE);

        declare("MAX_VALUE", Number.of(Double.MAX_VALUE));
        declare("MIN_VALUE", Number.of(Double.MIN_VALUE));
        declare("NEGATIVE_INFINITY", Number.of(Double.NEGATIVE_INFINITY));
        declare("POSITIVE_INFINITY", Number.of(Double.POSITIVE_INFINITY));
        declare("NaN", Number.of(Double.NaN));
        declare("BYTES", Number.of(Double.BYTES));
        declare("SIZE", Number.of(Double.SIZE));

        declare("parse", new JavaFunction("parse", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    Obj obj = scope.get("a");
                    if (obj instanceof Number) {
                        return obj;
                    } else if (obj instanceof Int) {
                        return Number.of(((Int) obj).value());
                    }
                    try {
                        return Number.of(Double.parseDouble(obj.toString()));
                    } catch (NumberFormatException e) {
                        throw new ComputeException(e);
                    }
                })
        );
    }
}
