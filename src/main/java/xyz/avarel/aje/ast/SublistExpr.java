package xyz.avarel.aje.ast;

import xyz.avarel.aje.runtime.Any;
import xyz.avarel.aje.runtime.Slice;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.numbers.Int;

import java.util.List;

public class SublistExpr implements Expr {
    private final Expr left;
    private final Expr startExpr;
    private final Expr endExpr;

    public SublistExpr(Expr left, Expr startExpr, Expr endExpr) {
        this.left = left;
        this.startExpr = startExpr;
        this.endExpr = endExpr;
    }

    @Override
    public Any compute() {
        Any obj = left.compute().identity();
        Any start = startExpr.compute().identity();
        Any end = endExpr.compute().identity();

        if (obj instanceof Slice && start instanceof Int && end instanceof Int) {
            Slice obj1 = (Slice) obj;
            Int start1 = (Int) start;
            Int end1 = (Int) end;

            if (start1.value() > end1.value()) {
                if (end1.value() < 0) {
                    end1 = Int.of(Math.floorMod(end1.value(), obj1.size()));
                } else {
                    return Undefined.VALUE;
                }
            }

            List<Any> sublist = obj1.subList(start1.value(), end1.value());

            return Slice.ofList(sublist);
        }

        return Undefined.VALUE;
    }

    @Override
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append("sublist\n");
        left.ast(builder, prefix + (isTail ? "    " : "│   "), false);
        builder.append('\n');
        startExpr.ast(builder, prefix + (isTail ? "    " : "│   "), false);
        builder.append('\n');
        endExpr.ast(builder, prefix + (isTail ? "    " : "│   "), true);
    }
}
