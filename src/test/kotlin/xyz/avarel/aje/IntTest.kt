package xyz.avarel.aje

import org.junit.Assert
import org.junit.Test
import xyz.avarel.aje.runtime.Any

class IntTest {
    @Test
    fun `addition`() {
        Assert.assertEquals(30, evaluate("10 + 20").toNative())
    }

    @Test
    fun `subtraction`() {
        Assert.assertEquals(-10, evaluate("10 - 20").toNative())
    }

    @Test
    fun `multiplication`() {
        Assert.assertEquals(200, evaluate("10 * 20").toNative())
    }

    @Test
    fun `implicit multiplication`() {
        Assert.assertEquals(2 * Math.PI, evaluate("2pi").toNative())
    }

    @Test
    fun `division`() {
        Assert.assertEquals(2, evaluate("20 / 10").toNative())
    }

    @Test
    fun `integer division by larger number`() {
        Assert.assertEquals(0, evaluate("10 / 20").toNative())
    }

    @Test
    fun `division by zero`() {
        Assert.assertEquals(java.lang.Double.POSITIVE_INFINITY, evaluate("1 / 0").toNative())
    }

    @Test
    fun `exponentiation`() {
        Assert.assertEquals(121, evaluate("11^2").toNative())
    }

    @Test
    fun `mod`() {
        Assert.assertEquals(8, evaluate("-2%10").toNative())
    }

    @Test
    fun `negative modulus`() {
        Assert.assertEquals(2, evaluate("12%10").toNative())
    }

    @Test
    fun `negative operation`() {
        Assert.assertEquals(-12, evaluate("-12").toNative())
    }

    @Test
    fun `equality`() {
        Assert.assertEquals(true, evaluate("2^4 == 4^2").toNative())
    }

    @Test
    fun `greater than`() {
        Assert.assertEquals(false, evaluate("60 > 60").toNative())
    }

    @Test
    fun `less than`() {
        Assert.assertEquals(true, evaluate("59 < 60").toNative())
    }

    @Test
    fun `greater than or equal to`() {
        Assert.assertEquals(true, evaluate("60 >= 60").toNative())
    }

    @Test
    fun `less than or equal to`() {
        Assert.assertEquals(true, evaluate("2 <= 3").toNative())
    }

    private fun evaluate(script: String): Any {
        val exp = Expression(script)
        return exp.compile().compute()
    }
}
