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
import xyz.avarel.aje.runtime.numbers.Complex

class ComplexTest {
    @Test
    fun `addition`() {
        Assert.assertEquals(Complex.of(2.0, 1.0), eval("2 + 1i"))
    }

    @Test
    fun `subtraction`() {
        Assert.assertEquals(Complex.of(2.0, 1.0), eval("3 + 2i - (1 + i)"))
    }

    @Test
    fun `multiplication`() {
        Assert.assertEquals(Complex.of(14.0, 46.0), eval("(8+2i)*(5i+3)"))
    }

    @Test
    fun `division`() {
        Assert.assertEquals(Complex.of(1.0), eval("6i / 6i"))
    }

    @Test
    fun `division 2`() {
        Assert.assertEquals(Complex.of(0.36, -1.52), eval("(5.0 + 6.0i) / (-3.0 + 4.0i)"))
    }

    @Test
    fun `exponentiation`() {
        Assert.assertEquals(Complex.of(1.0, 0.0), eval("i^1000"))
    }

    @Test
    fun `exponentiation 2`() {
        Assert.assertEquals(Complex.of(-3479.0, 1320.0), eval("(5i + 6) ^ 4"))
    }

    @Test
    fun `negative operation`() {
        Assert.assertEquals(Complex.of(-6.0, -5.0), eval("-(5i + 6)"))
    }

    @Test
    fun `equality`() {
        Assert.assertEquals(true, eval("(5i + 6) == (6 + 5i)").toNative())
    }
}
