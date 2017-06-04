/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
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

package xyz.avarel.aje.others;

import xyz.avarel.aje.Expression;
import xyz.avarel.aje.runtime.Obj;

class AJETest {
    public static void main(String[] args) {
        // Base expression.
        Expression exp = new Expression("sum(tau,2,i)");

        // Add a constant.
        exp.add("tau", new Expression("2 * pi"));

//        // Add a normal function.
//        exp.add("double", new NativeFunction(Numeric.TYPE) {
//            @Override
//            protected Obj eval(Obj target, List<Obj> arguments) {
//                return arguments.get(0).times(2);
//            }
//        });
//
//        // Add a varargs function.
//        exp.add("sum", new NativeFunction(true, Numeric.TYPE) {
//            @Override
//            protected Obj eval(Obj target, List<Obj> arguments) {
//                if (arguments.isEmpty()) return Int.of(0);
//                Obj accumulator = arguments.get(0);
//                for (int i = 1; i < arguments.size(); i++) {
//                    accumulator = accumulator.plus(arguments.get(i));
//                }
//                return accumulator;
//            }
//        });

        // Calculate into AJE object.
        Obj result = exp.compute();

        // Get the native representation of the object.
        // Each AJE object is mapped to a native object.
        Object obj = result.toNative();

        // Prints the result.
        System.out.println(result);
    }
}