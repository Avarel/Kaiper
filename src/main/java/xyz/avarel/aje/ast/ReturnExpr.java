package xyz.avarel.aje.ast;

import xyz.avarel.aje.parser.lexer.Position;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

public class ReturnExpr extends Expr {
    private final Expr expr;

    public ReturnExpr(Position position, Expr expr) {
        super(position);
        this.expr = expr;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return "return " + expr.toString();
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        expr.ast("return", builder, indent, true);
    }
}
