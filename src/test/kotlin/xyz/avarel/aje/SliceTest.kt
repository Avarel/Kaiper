package xyz.avarel.aje

import org.junit.Assert
import org.junit.Test

class SliceTest {
    @Test
    fun `declare`() {
        Assert.assertEquals("[1, 2, 3]", eval("[1,2,3]").toString())
    }

    @Test
    fun `addition`() {
        Assert.assertEquals("[3, 4, 5]", eval("[1,2,3] + [2]").toString())
    }

    @Test
    fun `subtraction`() {
        Assert.assertEquals("[-1, 0, 1]", eval("[1,2,3] - [2]").toString())
    }

    @Test
    fun `multiplication`() {
        Assert.assertEquals("[2, 6]", eval("[1,2,3] * [2, 3]").toString())
    }

    @Test
    fun `division`() {
        Assert.assertEquals("[0.5, 1.0, 1.5]", eval("[1,2,3] / [2.0]").toString())
    }

    @Test
    fun `exponentiation`() {
        Assert.assertEquals("[1, 4, 9, 16, 25, 36, 49, 64, 81, 100]", eval("[1..10]^2").toString())
    }

    @Test
    fun `negative operation`() {
        Assert.assertEquals("[-1, -4, -9, -16, -25, -36, -49, -64, -81, -100]", eval("-[1..10]^2").toString())
    }

    @Test
    fun `equality`() {
        Assert.assertEquals(true, eval("[1..3] == [1,2,3]").toNative())
    }
}
