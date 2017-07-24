package xyz.avarel.kaiper

tailrec fun factorial(x: Int, n: Int = x - 1): Int {
    println("meme")
    if (n != 0) {
        println("lol")
        return factorial(x * n, n - 1)
    } else {
        println("wew")
        return x
    }
}

tailrec fun factorial2(x: Int, n: Int = x - 1): Int {


    if (n != 0) {
        return factorial2(x * n, n - 1)
    } else {
        return x
    }
}