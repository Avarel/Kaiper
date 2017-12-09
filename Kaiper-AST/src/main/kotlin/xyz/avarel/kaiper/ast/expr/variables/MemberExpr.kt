package xyz.avarel.kaiper.ast.expr.variables

import xyz.avarel.kaiper.ast.ExprVisitor
import xyz.avarel.kaiper.ast.expr.Expr
import xyz.avarel.kaiper.lexer.Position

class MemberExpr(position: Position, val parent: Expr, val name: String) : Expr(position) {
    override fun <R, C> accept(visitor: ExprVisitor<R, C>, context: C): R {
        TODO()
    }
}
