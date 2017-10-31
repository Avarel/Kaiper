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

import xyz.avarel.kaiper.interpreter.ExprInterpreter;
import xyz.avarel.kaiper.runtime.collections.Range;
import xyz.avarel.kaiper.runtime.modules.NativeModule;

public class RangeModule extends NativeModule {
    public RangeModule(ExprInterpreter interpreter) {
        super("Obj");
        declare("TYPE", Range.TYPE);

        //        declare("length", new NativeFunc("length", "range") {
//            @Override
//            protected Obj eval(Map<String, Obj> arguments) {
//                return Int.of(((Range) arguments.get("range")).size());
//            }
//        });
//
//        declare("lastIndex", new NativeFunc("lastIndex", "range") {
//            @Override
//            protected Obj eval(Map<String, Obj> arguments) {
//                return Int.of(((Range) arguments.get("range")).size() - 1);
//            }
//        });
    }
}
