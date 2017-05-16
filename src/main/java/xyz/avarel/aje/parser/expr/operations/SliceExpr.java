package xyz.avarel.aje.parser.expr.operations;

import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.runtime.types.Slice;

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
}
