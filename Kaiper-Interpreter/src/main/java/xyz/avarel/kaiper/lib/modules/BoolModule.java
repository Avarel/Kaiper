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
import xyz.avarel.kaiper.interpreter.ExprInterpreter;
import xyz.avarel.kaiper.runtime.Bool;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.functions.JavaFunction;
import xyz.avarel.kaiper.runtime.modules.NativeModule;

public class BoolModule extends NativeModule {
    public BoolModule(ExprInterpreter interpreter) {
        super("Obj");
        declare("TYPE", Bool.TYPE);

        declare("parse", new JavaFunction("parse", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    Obj obj = scope.get("a");
                    if (obj instanceof Bool) {
                        return obj;
                    }
                    return Bool.of(Boolean.valueOf(obj.toString()));
                })
        );
    }
}
