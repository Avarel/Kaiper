package xyz.avarel.aje

import org.junit.Assert
import org.junit.Test

class DecimalTest {
    @Test
    fun `addition`() {
        Assert.assertEquals(30.0, eval("10.0 + 20").toNative())
    }

    @Test
    fun `subtraction`() {
        Assert.assertEquals(-10.0, eval("10 - 20.0").toNative())
    }

    @Test
    fun `multiplication`() {
        Assert.assertEquals(200.0, eval("10.0 * 20").toNative())
    }

    @Test
    fun `implicit multiplication`() {
        Assert.assertEquals(4.3 * Math.PI, eval("4.3pi").toNative())
    }

    @Test
    fun `division`() {
        Assert.assertEquals(2.0, eval("20 / 10.0").toNative())
    }

    @Test
    fun `division by larger number`() {
        Assert.assertEquals(0.5, eval("10.0 / 20").toNative())
    }

    @Test
    fun `division by zero`() {
        Assert.assertEquals(java.lang.Double.POSITIVE_INFINITY, eval("1 / 0.0").toNative())
    }

    @Test
    fun `exponentiation`() {
        Assert.assertEquals(121.0, eval("11.0^2").toNative())
    }

    @Test
    fun `mod`() {
        Assert.assertEquals(8.0, eval("-2%10.0").toNative())
    }

    @Test
    fun `negative modulus`() {
        Assert.assertEquals(2.0, eval("12.0%10").toNative())
    }

    @Test
    fun `negative operation`() {
        Assert.assertEquals(-12.0, eval("-12.0").toNative())
    }

    @Test
    fun `equality`() {
        Assert.assertEquals(true, eval("2.0^4 == 4^2.0").toNative())
    }

    @Test
    fun `equality with int`() {
        Assert.assertEquals(true, eval("2.0 == 2").toNative())
    }

    @Test
    fun `equality with complex`() {
        Assert.assertEquals(true, eval("2.0 == 2 + 0i").toNative())
    }

    @Test
    fun `greater than`() {
        Assert.assertEquals(false, eval("60.0 > 60").toNative())
    }

    @Test
    fun `less than`() {
        Assert.assertEquals(true, eval("59 < 60.0").toNative())
    }

    @Test
    fun `greater than or equal to`() {
        Assert.assertEquals(true, eval("60.0 >= 60").toNative())
    }

    @Test
    fun `less than or equal to`() {
        Assert.assertEquals(true, eval("2 <= 3.0").toNative())
    }
}