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

package xyz.avarel.kaiper.lib.std;

import xyz.avarel.kaiper.ast.pattern.PatternCase;
import xyz.avarel.kaiper.interpreter.ExprInterpreter;
import xyz.avarel.kaiper.lib.modules.*;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Str;
import xyz.avarel.kaiper.runtime.collections.Range;
import xyz.avarel.kaiper.runtime.functions.JavaFunction;
import xyz.avarel.kaiper.runtime.numbers.Int;
import xyz.avarel.kaiper.scope.Scope;

public class DefaultScope extends Scope<String, Obj> {
    public DefaultScope(ExprInterpreter interpreter) {
        put("str", new JavaFunction("str", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    Obj obj = scope.get("a");
                    if (obj instanceof Str) {
                        return obj;
                    }
                    return Str.of(obj.toString());
                })
        );
        put("range", new JavaFunction("range", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    int start = scope.get("from").as(Int.TYPE).value();
                    int end = scope.get("to").as(Int.TYPE).value();
                    return new Range(start, end);
                })
        );
        put("rangeex", new JavaFunction("rangeex", interpreter)
                .addDispatch(new PatternCase("a"), scope -> {
                    int start = scope.get("from").as(Int.TYPE).value();
                    int end = scope.get("to").as(Int.TYPE).value() - 1;
                    return new Range(start, end);
                })
        );
        put("not", new JavaFunction("not", interpreter)
                .addDispatch(new PatternCase("a"), scope -> scope.get("a").negate())
        );

        // todo figure a way to do unary plus
        // probably by having unary operator parser return an invocation like +(1)
//        put("+", new JavaFunction("+", interpreter)
//                .addDispatch(new PatternCase("a", "b"), scope -> scope.get("a").plus(scope.get("b"))));

        put("Object", new ObjModule(interpreter));
        put("Math", new MathModule(interpreter));
        put("Tuple", new TupleModule(interpreter));
        put("Int", new IntModule(interpreter));
        put("Number", new NumberModule(interpreter));
        put("Boolean", new BoolModule(interpreter));
        put("Array", new ArrayModule(interpreter));
        put("Range", new RangeModule(interpreter));
        put("Dictionary", new DictionaryModule(interpreter));
        put("String", new StrModule(interpreter));
        put("Function", new FunctionModule(interpreter));
        put("Null", new NullModule(interpreter));
    }
}
