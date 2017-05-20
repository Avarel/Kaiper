package xyz.avarel.aje

import xyz.avarel.aje.runtime.Any

internal fun eval(script: String): Any {
    val exp = Expression(script)
    return exp.compile().compute()
}