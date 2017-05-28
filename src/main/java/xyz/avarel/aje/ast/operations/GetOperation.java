package xyz.avarel.aje.ast.operations;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.parser.lexer.Position;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

public class GetOperation extends Expr {
    private final Expr left;
    private final Expr indexExpr;

    public GetOperation(Position position, Expr left, Expr indexExpr) {
        super(position);
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
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append("get\n");
        left.ast("target", builder, indent + (isTail ? "    " : "│   "), false);
        builder.append('\n');
        indexExpr.ast("key", builder, indent + (isTail ? "    " : "│   "), true);
    }

    @Override
    public String toString() {
        return "get";
    }
}
