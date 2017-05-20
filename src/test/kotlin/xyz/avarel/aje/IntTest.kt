package xyz.avarel.aje

import org.junit.Assert
import org.junit.Test

class IntTest {
    @Test
    fun `addition`() {
        Assert.assertEquals(30, eval("10 + 20").toNative())
    }

    @Test
    fun `subtraction`() {
        Assert.assertEquals(-10, eval("10 - 20").toNative())
    }

    @Test
    fun `multiplication`() {
        Assert.assertEquals(200, eval("10 * 20").toNative())
    }

    @Test
    fun `implicit multiplication`() {
        Assert.assertEquals(2 * Math.PI, eval("2pi").toNative())
    }

    @Test
    fun `division`() {
        Assert.assertEquals(2, eval("20 / 10").toNative())
    }

    @Test
    fun `integer division by larger number`() {
        Assert.assertEquals(0, eval("10 / 20").toNative())
    }

    @Test
    fun `division by zero`() {
        Assert.assertEquals(java.lang.Double.POSITIVE_INFINITY, eval("1 / 0").toNative())
    }

    @Test
    fun `exponentiation`() {
        Assert.assertEquals(121, eval("11^2").toNative())
    }

    @Test
    fun `mod`() {
        Assert.assertEquals(8, eval("-2%10").toNative())
    }

    @Test
    fun `negative modulus`() {
        Assert.assertEquals(2, eval("12%10").toNative())
    }

    @Test
    fun `negative operation`() {
        Assert.assertEquals(-12, eval("-12").toNative())
    }

    @Test
    fun `equality`() {
        Assert.assertEquals(true, eval("2^4 == 4^2").toNative())
    }

    @Test
    fun `greater than`() {
        Assert.assertEquals(false, eval("60 > 60").toNative())
    }

    @Test
    fun `less than`() {
        Assert.assertEquals(true, eval("59 < 60").toNative())
    }

    @Test
    fun `greater than or equal to`() {
        Assert.assertEquals(true, eval("60 >= 60").toNative())
    }

    @Test
    fun `less than or equal to`() {
        Assert.assertEquals(true, eval("2 <= 3").toNative())
    }
}
