package xyz.avarel.kaiper.ast.expr.variables;

import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.expr.Expr;
import xyz.avarel.kaiper.lexer.Position;

public class MemberExpr extends Expr {
    private final Expr parent;
    private final String name;

    public MemberExpr(Position position, Expr parent, String name) {
        super(position);
        this.parent = parent;
        this.name = name;
    }

    public Expr getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C context) {
//      todo  return visitor.visit(this, context);
        return null;
    }
}
