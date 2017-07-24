package xyz.avarel.kaiper

tailrec fun factorial(x: Int, n: Int = x - 1): Int {
    println("meme")
    return if (n != 0) {
        println("lol")
        factorial(x * n, n - 1)
    } else {
        x
    }
}