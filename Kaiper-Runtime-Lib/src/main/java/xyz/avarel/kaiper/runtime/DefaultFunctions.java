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

package xyz.avarel.kaiper.runtime;

import xyz.avarel.kaiper.runtime.collections.Range;
import xyz.avarel.kaiper.runtime.functions.Func;
import xyz.avarel.kaiper.runtime.functions.NativeFunc;
import xyz.avarel.kaiper.runtime.numbers.Int;

import java.util.Map;

public enum DefaultFunctions {
    STR(new NativeFunc("str", "a") {
        @Override
        protected Obj eval(Map<String, Obj> arguments) {
            Obj obj = arguments.get("a");
            if (obj instanceof Str) {
                return obj;
            }
            return Str.of(obj.toString());
        }
    }),

    RANGE(new NativeFunc("range", "from", "to") {
        @Override
        protected Obj eval(Map<String, Obj> arguments) {
            int start = arguments.get("from").as(Int.TYPE).value();
            int end = arguments.get("to").as(Int.TYPE).value();
            return new Range(start, end);
        }
    }),
    RANGE_EX(new NativeFunc("rangeex", "from", "to") {
        @Override
        protected Obj eval(Map<String, Obj> arguments) {
            int start = arguments.get("from").as(Int.TYPE).value();
            int end = arguments.get("to").as(Int.TYPE).value() - 1;
            return new Range(start, end);
        }
    }),

    TUPLE(new Func("t") {
        @Override
        public int getArity() {
            return 0;
        }

        @Override
        public Obj invoke(Obj argument) {
            return argument;
        }
    }),

    NOT(new NativeFunc("not", "function") {
        @Override
        protected Obj eval(Map<String, Obj> arguments) {
            return new NativeFunc("not") {
                @Override
                protected Obj eval(Map<String, Obj> arguments0) {
                    return arguments.get("function").invoke(new Tuple(arguments.get("function"))).negate();
                }
            };
        }
    });

    private final Func function;

    DefaultFunctions(Func function) {
        this.function = function;
    }

    public Func get() {
        return function;
    }
}
