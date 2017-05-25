package xyz.avarel.aje.ast.operations;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

public class GetOperation implements Expr {
    private final Expr left;
    private final Expr indexExpr;

    public GetOperation(Expr left, Expr indexExpr) {
        this.left = left;
        this.indexExpr = indexExpr;
    }

    public Expr getLeft() {
        return left;
    }

    public Expr getArgument() {
        return indexExpr;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append("list index\n");
        left.ast(builder, prefix + (isTail ? "    " : "│   "), false);
        builder.append('\n');
        indexExpr.ast(builder, prefix + (isTail ? "    " : "│   "), true);
    }

    @Override
    public String toString() {
        return "get";
    }
}
