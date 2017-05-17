package xyz.avarel.aje.parser.expr.operations;

import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.runtime.Slice;

import java.util.List;

public class SliceExpr implements Expr {
    private final List<Expr> exprs;

    public SliceExpr(List<Expr> exprs) {
        this.exprs = exprs;
    }

    @Override
    public Slice compute() {
        Slice slice = new Slice();

        for (Expr expr : exprs) {
            if (expr instanceof RangeExpr) {
                slice.addAll((Slice) expr.compute());
                continue;
            }
            slice.add(expr.compute());
        }

        return slice;
    }

    @Override
    public void ast(StringBuilder builder, String indent) {
        builder.append(indent).append("list\n");
        for (int i = 0; i < exprs.size(); i++) {
            Expr expr = exprs.get(i);
            expr.ast(builder, indent + "│ ");
            if (i < exprs.size() - 1) builder.append('\n');
        }
    }
}
