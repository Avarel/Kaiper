package xyz.avarel.aje.parser.expr.operations;

import xyz.avarel.aje.parser.expr.Expr;
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
        return left.compute().rangeTo(right.compute());
    }

    @Override
    public void ast(StringBuilder builder, String indent) {
        builder.append(indent).append("range\n");
        left.ast(builder, indent + "│ ");
        builder.append('\n');
        right.ast(builder, indent + "│ ");
    }
}
