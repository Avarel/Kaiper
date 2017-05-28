package xyz.avarel.aje

import org.junit.Test

class ExprTest {
    @Test
    fun `check compile`() {
        eval("1")
        eval("42")
        eval("1+2^3")
        eval("2*(3*4)")

        eval("1.235")
        eval("-2.0/17")
        eval("3.0+2.5")

        eval("3 >= 2")
        eval("true && false")

        eval("i^2")
        eval("3i")
        eval("(8+2i)*(5i+3)")

        eval("[1,2,3] == [1..3]")
        eval("[1,2,3] + [1]")

        eval("func(x) = { x + 2 }")
        eval("{ x, y -> x ^ y }")

        eval("func f(x) = x + 2; f(2) == 4")
        eval("func isEven(x) { x % 2 == 0 }; [1..20] |> filter(isEven)")

        eval("var add = { x, y -> x + y }; [1..10] |> fold(0, add) == 55")
        eval("[1..10] |> fold(1, { x, y -> x * y })")

        eval("[[1, 2, 3], [1, 5, 8, 9, 10], [1..50]] |> map(_.size)")
        eval("map([1..10], _ ^ 2) == [1, 4, 9, 16, 25, 36, 49, 64, 81, 100]")

        eval("sqrt(-1)")
        eval("sqrt(i)")
        eval("(1+i)^2")
        eval("(8+2i)*(5i+3)")

        eval("[1,2,3][1]")
        eval("[1..3] + [2]")
        eval("var x = [50..60]; x[5]")
        eval("var x = [100..200]; x[25:30]")

        eval("[1..10] |> map(_ ^ 2)")
        eval("var add = { x, y -> x + y }; [1..10] |> fold(0, add)")
        eval("func isEven(x) { x % 2 == 0 }; [1..20] |> filter(isEven)")
        eval("[1..10] |> fold(1, { x, y -> x * y })")
    }
}
