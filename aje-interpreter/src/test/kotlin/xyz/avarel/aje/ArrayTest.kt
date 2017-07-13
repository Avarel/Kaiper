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

import org.junit.Assert
import org.junit.Test

class ArrayTest {
    @Test
    fun `declare`() {
        Assert.assertEquals(eval("[1, 2, 3]"), eval("[1,2,3]"))
    }

    @Test
    fun `addition`() {
        Assert.assertEquals(eval("[3, 4, 5]"), eval("[1,2,3] + [2]"))
    }

    @Test
    fun `subtraction`() {
        Assert.assertEquals(eval("[-1, 0, 1]"), eval("[1,2,3] - [2]"))
    }

    @Test
    fun `multiplication`() {
        Assert.assertEquals(eval("[2, 6]"), eval("[1,2,3] * [2, 3]"))
    }

    @Test
    fun `division`() {
        Assert.assertEquals(eval("[0.5, 1.0, 1.5]"), eval("[1,2,3] / [2.0]"))
    }

    @Test
    fun `exponentiation`() {
        Assert.assertEquals(eval("[1, 4, 9, 16, 25, 36, 49, 64, 81, 100]"), eval("[1..10]^2"))
    }

    @Test
    fun `negative operation`() {
        Assert.assertEquals(eval("[-1, -4, -9, -16, -25, -36, -49, -64, -81, -100]"), eval("-[1..10]^2"))
    }

    @Test
    fun `equality`() {
        Assert.assertEquals(true, eval("[1..3] == [1,2,3]").toJava())
    }
}
