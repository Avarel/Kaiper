package xyz.avarel.aje

import org.junit.Assert
import org.junit.Test
import xyz.avarel.aje.runtime.Any

class DecimalTest {
    @Test
    fun `addition`() {
        Assert.assertEquals(evaluate("10.0 + 20").toNative(), 30.0)
    }

    @Test
    fun `subtraction`() {
        Assert.assertEquals(-10.0, evaluate("10 - 20.0").toNative())
    }

    @Test
    fun `multiplication`() {
        Assert.assertEquals(200.0, evaluate("10.0 * 20").toNative())
    }

    @Test
    fun `implicit multiplication`() {
        Assert.assertEquals(4.3 * Math.PI, evaluate("4.3pi").toNative())
    }

    @Test
    fun `division`() {
        Assert.assertEquals(2.0, evaluate("20 / 10.0").toNative())
    }

    @Test
    fun `division by larger number`() {
        Assert.assertEquals(0.5, evaluate("10.0 / 20").toNative())
    }

    @Test
    fun `division by zero`() {
        Assert.assertEquals(java.lang.Double.POSITIVE_INFINITY, evaluate("1 / 0.0").toNative())
    }

    @Test
    fun `exponentiation`() {
        Assert.assertEquals(121.0, evaluate("11.0^2").toNative())
    }

    @Test
    fun `mod`() {
        Assert.assertEquals(8.0, evaluate("-2%10.0").toNative())
    }

    @Test
    fun `negative modulus`() {
        Assert.assertEquals(2.0, evaluate("12.0%10").toNative())
    }

    @Test
    fun `negative operation`() {
        Assert.assertEquals(-12.0, evaluate("-12.0").toNative())
    }

    @Test
    fun `equality`() {
        Assert.assertEquals(true, evaluate("2.0^4 == 4^2.0").toNative())
    }

    @Test
    fun `greater than`() {
        Assert.assertEquals(false, evaluate("60.0 > 60").toNative())
    }

    @Test
    fun `less than`() {
        Assert.assertEquals(true, evaluate("59 < 60.0").toNative())
    }

    @Test
    fun `greater than or equal to`() {
        Assert.assertEquals(true, evaluate("60.0 >= 60").toNative())
    }

    @Test
    fun `less than or equal to`() {
        Assert.assertEquals(true, evaluate("2 <= 3.0").toNative())
    }

    private fun evaluate(script: String): Any {
        val exp = Expression(script)
        return exp.compile().compute()
    }
}
