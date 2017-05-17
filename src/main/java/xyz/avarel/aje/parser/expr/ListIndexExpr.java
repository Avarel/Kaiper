package xyz.avarel.aje.parser.expr;

import xyz.avarel.aje.runtime.Any;
import xyz.avarel.aje.runtime.Slice;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.numbers.Int;

public class ListIndexExpr implements Expr {
    private final Expr left;
    private final Expr indexExpr;

    public ListIndexExpr(Expr left, Expr indexExpr) {
        this.left = left;
        this.indexExpr = indexExpr;
    }

    @Override
    public Any compute() {
        Any list = left.compute();
        Any index = indexExpr.compute();

        if (list instanceof Slice && index instanceof Int) {
            return ((Slice) list).get(((Int) index).value());
        }

        return Undefined.VALUE;
    }

    @Override
    public void ast(StringBuilder builder, String indent) {
        builder.append(indent).append("binary operation\n");
        left.ast(builder, indent + "│ ");
        builder.append('\n');
        indexExpr.ast(builder, indent + "│ ");
    }
}
