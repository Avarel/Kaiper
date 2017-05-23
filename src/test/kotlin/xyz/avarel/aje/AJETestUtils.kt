package xyz.avarel.aje

import xyz.avarel.aje.runtime.Obj

internal fun eval(script: String): Obj {
    val exp = Expression(script)
    return exp.compile().compute()
}