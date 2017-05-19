package xyz.avarel.aje.ast.operations;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.runtime.Any;

public class RangeExpr implements Expr {
    private final Expr left;
    private final Expr right;

    public RangeExpr(Expr left, Expr right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Any compute() {
        return left.compute().identity().rangeTo(right.compute().identity());
    }

    @Override
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append("range\n");
        left.ast(builder, prefix + (isTail ? "    " : "│   "), false);
        builder.append('\n');
        right.ast(builder, prefix + (isTail ? "    " : "│   "), true);
    }
}
