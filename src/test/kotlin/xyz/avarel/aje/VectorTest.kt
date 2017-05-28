package xyz.avarel.aje

import org.junit.Assert
import org.junit.Test

class VectorTest {
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
        Assert.assertEquals(true, eval("[1..3] == [1,2,3]").toNative())
    }
}
