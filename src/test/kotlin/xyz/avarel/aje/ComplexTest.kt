package xyz.avarel.aje

import org.junit.Assert
import org.junit.Test
import xyz.avarel.aje.runtime.Any
import xyz.avarel.aje.runtime.numbers.Complex

class ComplexTest {
    @Test
    fun `addition`() {
        Assert.assertEquals(Complex.of(2.0, 1.0), evaluate("2 + 1i"))
    }

    @Test
    fun `subtraction`() {
        Assert.assertEquals(Complex.of(2.0, 1.0), evaluate("3 + 2i - (1 + i)"))
    }

    @Test
    fun `multiplication`() {
        Assert.assertEquals(Complex.of(14.0, 46.0), evaluate("(8+2i)*(5i+3)"))
    }

    @Test
    fun `division`() {
        Assert.assertEquals(Complex.of(1.0), evaluate("6i / 6i"))
    }

    @Test
    fun `division 2`() {
        Assert.assertEquals(Complex.of(0.36, -1.52), evaluate("(5.0 + 6.0i) / (-3.0 + 4.0i)"))
    }

    @Test
    fun `exponentiation`() {
        Assert.assertEquals(Complex.of(1.0, 0.0), evaluate("i^1000"))
    }

    @Test
    fun `exponentiation 2`() {
        Assert.assertEquals(Complex.of(-3479.0, 1320.0), evaluate("(5i + 6) ^ 4"))
    }

    @Test
    fun `negative operation`() {
        Assert.assertEquals(Complex.of(-6.0, -5.0), evaluate("-(5i + 6)"))
    }

    @Test
    fun `equality`() {
        Assert.assertEquals(true, evaluate("(5i + 6) == (6 + 5i)").toNative())
    }

    private fun evaluate(script: String): Any {
        val exp = Expression(script)
        return exp.compile().compute()
    }
}
