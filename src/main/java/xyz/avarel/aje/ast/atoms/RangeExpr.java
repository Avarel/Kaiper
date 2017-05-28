package xyz.avarel.aje.ast.atoms;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.parser.lexer.Position;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

public class RangeExpr extends Expr {
    private final Expr left;
    private final Expr right;
    private final boolean exclusive;

    public RangeExpr(Position position, Expr left, Expr right, boolean exclusive) {
        super(position);
        this.left = left;
        this.right = right;
        this.exclusive = exclusive;
    }

    public Expr getLeft() {
        return left;
    }

    public Expr getRight() {
        return right;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append("range").append(exclusive ? " exclusive" : " inclusive").append('\n');
        left.ast("start", builder, indent + (isTail ? "    " : "│   "), false);
        builder.append('\n');
        right.ast("end", builder, indent + (isTail ? "    " : "│   "), true);
    }

    @Override
    public String toString() {
        return "range";
    }
}
