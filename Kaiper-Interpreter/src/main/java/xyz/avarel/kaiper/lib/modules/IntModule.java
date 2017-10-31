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

public class IntModule extends NativeModule {
    public IntModule(ExprInterpreter interpreter) {
        super("Int");
        declare("TYPE", Int.TYPE);

        declare("MAX_VALUE", Int.of(Integer.MAX_VALUE));
        declare("MIN_VALUE", Int.of(Integer.MIN_VALUE));
        declare("BYTES", Int.of(Integer.BYTES));
        declare("SIZE", Int.of(Integer.SIZE));

        declare("parse", new JavaFunction("parse", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    Obj obj = scope.get("a");
                    if (obj instanceof Int) {
                        return obj;
                    } else if (obj instanceof Number) {
                        return Int.of((int) ((Number) obj).value());
                    }
                    try {
                        return Int.of(Integer.parseInt(obj.toString()));
                    } catch (NumberFormatException e) {
                        throw new ComputeException(e);
                    }
                })
        );
    }
}
