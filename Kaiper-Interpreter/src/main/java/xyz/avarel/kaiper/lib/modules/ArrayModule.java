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
import xyz.avarel.kaiper.runtime.collections.Array;
import xyz.avarel.kaiper.runtime.modules.NativeModule;

public class ArrayModule extends NativeModule {
    public ArrayModule(ExprInterpreter interpreter) {
        super("Obj");
        declare("TYPE", Array.TYPE);


//        declare("length", new NativeFunc("length", "array") {
//            @Override
//            protected Obj eval(Map<String, Obj> arguments) {
//                return Int.of(arguments.get("array").as(Array.TYPE).size());
//            }
//        });
//
//        declare("lastIndex", new NativeFunc("lastIndex", "array") {
//            @Override
//            protected Obj eval(Map<String, Obj> arguments) {
//                return Int.of(arguments.get("array").as(Array.TYPE).size() - 1);
//            }
//        });
//
//        declare("append", new NativeFunc("append", "array", "elements") {
//            @Override
//            protected Obj eval(Map<String, Obj> arguments) {
//                Array array = arguments.get("array").as(Array.TYPE);
//                array.list.addAll(arguments.get("elements").as(Array.TYPE).list);
//                return array;
//            }
//        });
//
//        declare("each", new NativeFunc("each", "array", "action") {
//            @Override
//            protected Obj eval(Map<String, Obj> arguments) {
//                Function action = arguments.get("action").as(Function.TYPE);
//
//                for (Obj obj : arguments.get("array").as(Array.TYPE)) {
//                    action.invoke(new Tuple(obj));
//                }
//                return Null.VALUE;
//            }
//        });
//
//        declare("map", new NativeFunc("map", "array", "transform") {
//            @Override
//            protected Obj eval(Map<String, Obj> arguments) {
//                Function transform = arguments.get("transform").as(Function.TYPE);
//
//                Array array = new Array();
//                for (Obj obj : arguments.get("array").as(Array.TYPE)) {
//                    array.add(transform.invoke(new Tuple(obj)));
//                }
//                return array;
//            }
//        });
//
//        declare("filter", new NativeFunc("filter", "array", "predicate") {
//            @Override
//            protected Obj eval(Map<String, Obj> arguments) {
//                Function predicate = arguments.get("predicate").as(Function.TYPE);
//
//                Array array = new Array();
//                for (Obj obj : arguments.get("array").as(Array.TYPE)) {
//                    Bool condition = (Bool) predicate.invoke(new Tuple(obj));
//                    if (condition == Bool.TRUE) array.add(obj);
//                }
//                return array;
//            }
//        });
//
//        declare("fold",
//                new NativeFunc("fold", "array", "accumulator", "operation") {
//                    @Override
//                    protected Obj eval(Map<String, Obj> arguments) {
//                        Obj accumulator = arguments.get("accumulator");
//                        Function operation = arguments.get("operation").as(Function.TYPE);
//
//                        for (Obj obj : arguments.get("array").as(Array.TYPE)) {
//                            accumulator = operation.invoke(new Tuple(accumulator, obj));
//                        }
//                        return accumulator;
//                    }
//                });
//
//        declare("slice", new NativeFunc("slice",
//                new VariableRTPattern("self"),
//                new VariableRTPattern("start", true),
//                new VariableRTPattern("end", true),
//                new VariableRTPattern("step", true)
//        ) {
//            @Override
//            protected Obj eval(Map<String, Obj> arguments) {
//                Array array = arguments.get("self").as(Array.TYPE);
//                Obj startObj = arguments.get("start");
//                Obj endObj = arguments.get("end");
//                Obj stepObj = arguments.get("step");
//
//                int start, end, step;
//
//                if (startObj == Null.VALUE) {
//                    start = 0;
//                } else {
//                    start = startObj.as(Int.TYPE).value();
//                    if (start < 0) start += array.size();
//                }
//
//                if (endObj == Null.VALUE) {
//                    end = array.size();
//                } else {
//                    end = endObj.as(Int.TYPE).value();
//                    if (end < 0) end += array.size();
//                }
//
//                if (stepObj == Null.VALUE) {
//                    step = 1;
//                } else {
//                    step = stepObj.as(Int.TYPE).value();
//                }
//
//                if (step == 1) {
//                    return Array.ofList(array.subList(Math.max(0, start), Math.min(array.size(), end)));
//                } else {
//                    if (step > 0) {
//                        List<Obj> list = new ArrayList<>();
//
//                        for (int i = start; i < end; i += step) {
//                            list.add(array.get(i));
//                        }
//
//                        return new Array(list);
//                    } else if (step < 0) {
//                        List<Obj> list = new ArrayList<>();
//
//                        for (int i = end - 1; i >= start; i += step) {
//                            list.add(array.get(i));
//                        }
//
//                        return new Array(list);
//                    } else { // step == 0
//                        return Null.VALUE;
//                    }
//                }
//            }
//        });
    }
}
