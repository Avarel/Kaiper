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

package xyz.avarel.kaiper.others;

import xyz.avarel.kaiper.CompiledExpr;
import xyz.avarel.kaiper.Expression;

public class SpeedTester {
    public static void main(String[] args) {
        // Performance hoggers
        // [1,2,[3,4,[5,6,[7,8],90, [91, 92],100]],9,10,[11, [50,52],12],13]
        // (8+2i)*(5i+3)
        // [1..10] * [10]
        String script = "1+2";
        int testsAmt = 10000;

        testPrecompiledSpeed(script, 100);
        testRecompileSpeed(script, 100);

        System.out.println(testsAmt + " Tests");
        System.out.println("Precompiled: " + testPrecompiledSpeed(script, testsAmt) + "ns avg");
        System.out.println("  Recompile: " + testRecompileSpeed(script, testsAmt) + "ns avg");
    }

    private static long testPrecompiledSpeed(String script, long tests) {
        Expression exp = new Expression(script);

        CompiledExpr expr = exp.compile();

        long start = System.nanoTime();
        for (int i = 0; i < tests; i++) {
            expr.compute();
        }
        long end = System.nanoTime();

        return (end - start) / tests;
    }

    private static long testRecompileSpeed(String script, long tests) {
        long start = System.nanoTime();
        for (int i = 0; i < tests; i++) {
            Expression exp = new Expression(script);
            CompiledExpr expr = exp.compile();
            expr.compute();
        }
        long end = System.nanoTime();

        return (end - start) / tests;
    }
}
