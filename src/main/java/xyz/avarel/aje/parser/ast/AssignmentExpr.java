package xyz.avarel.aje.parser.ast;

import xyz.avarel.aje.runtime.Any;

public class AssignmentExpr implements Expr {
    private final Expr left;
    private final Expr right;

    public AssignmentExpr(Expr left, Expr right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Any compute() {
        return left.compute().set(right.compute());
    }

    @Override
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append("set\n");
        left.ast(builder, prefix + (isTail ? "    " : "│   "), false);
        builder.append('\n');
        right.ast(builder, prefix + (isTail ? "    " : "│   "), true);
    }
}
