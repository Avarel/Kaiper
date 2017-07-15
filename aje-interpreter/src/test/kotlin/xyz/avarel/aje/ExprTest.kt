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

package xyz.avarel.aje

import org.junit.Test

class ExprTest {
    @Test
    fun `check compile`() {
        eval("1")
        eval("42")
        eval("1+2^3")
        eval("2*(3*4)")

        eval("1.235")
        eval("-2.0/17")
        eval("3.0+2.5")

        eval("3 >= 2")
        eval("true && false")

        eval("[1,2,3] == [1..3]")

        eval("def(x) = { x + 2 }")
        eval("{ x, y -> x ^ y }")

        eval("def f(x) = x + 2; f(2) == 4")
        eval("def isEven(x) { x % 2 == 0 }; [1..20] |> Array.filter(isEven)")

        eval("let add = { x, y -> x + y }; [1..10] |> Array.fold(0, add) == 55")
        eval("[1..10] |> Array.fold(1, { x, y -> x * y })")

        eval("[[1, 2, 3], [1, 5, 8, 9, 10], [1..50]] |> Array.map(_.size)")
        eval("Array.map([1..10], _ ^ 2) == [1, 4, 9, 16, 25, 36, 49, 64, 81, 100]")

        eval("Math.sqrt(-1)")

        eval("[1,2,3][1]")
        eval("let x = [50..60]; x[5]")
        eval("let x = [100..200]; x[25:30]")

        eval("[1..10] |> Array.map(_ ^ 2)")
        eval("let add = { x, y -> x + y }; [1..10] |> Array.fold(0, add)")
        eval("def isEven(x) { x % 2 == 0 }; [1..20] |> Array.filter(isEven)")
        eval("[1..10] |> Array.fold(1, { x, y -> x * y })")
    }
}
