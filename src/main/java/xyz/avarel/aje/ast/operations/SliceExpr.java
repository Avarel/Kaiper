package xyz.avarel.aje.ast.operations;

import xyz.avarel.aje.ast.Expr;
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
                slice.addAll((Slice) expr.compute().identity());
                continue;
            }
            slice.add(expr.compute().identity());
        }

        return slice;
    }

    @Override
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append("list\n");
        for (int i = 0; i < exprs.size() - 1; i++) {
            exprs.get(i).ast(builder, prefix + (isTail ? "    " : "│   "), false);
            builder.append('\n');
        }
        if (exprs.size() > 0) {
            exprs.get(exprs.size() - 1).ast(builder, prefix + (isTail ? "    " : "│   "), true);
        }
    }
}
