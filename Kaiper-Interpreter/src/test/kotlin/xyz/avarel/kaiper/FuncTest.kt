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

package xyz.avarel.kaiper

import org.junit.Assert
import org.junit.Test

class FuncTest {
    val evaluator = KaiperREPL().apply {
        eval("def isEven0(x) { x % 2 == 0 }")
        eval("def isEven1(x) { x % 2 == 0 }")
        eval("def isEven2(x) = x % 2 == 0")
        eval("let isEven3 = { x -> x % 2 == 0 }")
        eval("let isEven4 = _ % 2 == 0")

        eval("let add = { x, y -> x + y }")
    }

    @Test
    fun `alternative syntax`() {
        Assert.assertEquals(evaluator.eval("isEven0(1)"), evaluator.eval("isEven1(1)"))
        Assert.assertEquals(evaluator.eval("isEven0(1)"), evaluator.eval("isEven2(1)"))
        Assert.assertEquals(evaluator.eval("isEven0(1)"), evaluator.eval("isEven3(1)"))
        Assert.assertNotEquals(evaluator.eval("isEven0(1)"), evaluator.eval("isEven4(2)"))

        Assert.assertEquals(evaluator.eval("isEven1(1)"), evaluator.eval("isEven0(1)"))
        Assert.assertEquals(evaluator.eval("isEven1(1)"), evaluator.eval("isEven2(1)"))
        Assert.assertNotEquals(evaluator.eval("isEven1(2)"), evaluator.eval("isEven3(1)"))
        Assert.assertEquals(evaluator.eval("isEven1(1)"), evaluator.eval("isEven4(1)"))

        Assert.assertEquals(evaluator.eval("isEven2(1)"), evaluator.eval("isEven0(1)"))
        Assert.assertNotEquals(evaluator.eval("isEven2(3)"), evaluator.eval("isEven1(0)"))
        Assert.assertEquals(evaluator.eval("isEven2(1)"), evaluator.eval("isEven3(1)"))
        Assert.assertEquals(evaluator.eval("isEven2(1)"), evaluator.eval("isEven4(1)"))

        Assert.assertNotEquals(evaluator.eval("isEven3(5)"), evaluator.eval("isEven0(2)"))
        Assert.assertEquals(evaluator.eval("isEven3(1)"), evaluator.eval("isEven1(1)"))
        Assert.assertEquals(evaluator.eval("isEven3(1)"), evaluator.eval("isEven2(1)"))
        Assert.assertEquals(evaluator.eval("isEven3(1)"), evaluator.eval("isEven4(1)"))

        Assert.assertEquals(evaluator.eval("isEven4(1)"), evaluator.eval("isEven0(1)"))
        Assert.assertNotEquals(evaluator.eval("isEven4(4)"), evaluator.eval("isEven1(7)"))
        Assert.assertNotEquals(evaluator.eval("isEven4(0)"), evaluator.eval("isEven2(9)"))
        Assert.assertEquals(evaluator.eval("isEven4(1)"), evaluator.eval("isEven3(1)"))

    }

    @Test
    fun `implicit vs named`() {
        Assert.assertEquals(evaluator.eval("[1..10] |> Array.filter(_ % 2 == 0)"), evaluator.eval("[1..10] |> Array.filter(isEven0)"))
    }

    @Test
    fun `pipe forward`() {
        Assert.assertEquals(evaluator.eval("[[1, 2, 3], [1, 5, 8, 9, 10], [1..50]] |> Array.map(_.size)"), eval("[3, 5, 50]"))
    }

    @Test
    fun `higher order map`() {
        Assert.assertEquals(evaluator.eval("Array.map([1..10], _ ^ 2)"), eval("[1, 4, 9, 16, 25, 36, 49, 64, 81, 100]"))
    }

    @Test
    fun `higher order filter` () {
        Assert.assertEquals(evaluator.eval("[1..10] |> Array.filter(_ % 2 == 0)"), eval("[2,4,6,8,10]"))
    }

    @Test
    fun `higher order fold`() {
        Assert.assertEquals(evaluator.eval("[1..10] |> Array.fold(0, add)"), eval("55"))
    }
}
