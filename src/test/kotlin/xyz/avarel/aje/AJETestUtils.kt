package xyz.avarel.aje

import xyz.avarel.aje.ast.ExprVisitor
import xyz.avarel.aje.runtime.Obj
import xyz.avarel.aje.runtime.pool.DefaultScope

internal fun eval(script: String): Obj {
    val exp = Expression(script)
    return exp.compile().accept(ExprVisitor(), DefaultScope.INSTANCE.subPool())
}