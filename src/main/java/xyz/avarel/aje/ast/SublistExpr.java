package xyz.avarel.aje.ast;

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.pool.Scope;

public class SublistExpr implements Expr {
    private final Expr left;
    private final Expr start;
    private final Expr endExpr;

    public SublistExpr(Expr left, Expr start, Expr endExpr) {
        this.left = left;
        this.start = start;
        this.endExpr = endExpr;
    }

    public Expr getLeft() {
        return left;
    }

    public Expr getStart() {
        return start;
    }

    public Expr getEnd() {
        return endExpr;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append("sublist\n");
        left.ast(builder, prefix + (isTail ? "    " : "│   "), false);
        builder.append('\n');
        start.ast(builder, prefix + (isTail ? "    " : "│   "), false);
        builder.append('\n');
        endExpr.ast(builder, prefix + (isTail ? "    " : "│   "), true);
    }
}
