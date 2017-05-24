package xyz.avarel.aje.ast.operations;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.pool.Scope;

public class SliceOperation implements Expr {
    private final Expr left;
    private final Expr start;
    private final Expr end;
    private final Expr step;

    public SliceOperation(Expr left, Expr start, Expr end, Expr step) {
        this.left = left;
        this.start = start;
        this.end = end;

        this.step = step;
    }

    public Expr getLeft() {
        return left;
    }

    public Expr getStart() {
        return start;
    }

    public Expr getEnd() {
        return end;
    }

    public Expr getStep() {
        return step;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append("sublist\n");

        left.ast(builder, prefix + (isTail ? "    " : "│   "), false);

        builder.append('\n');
        if (start != null) {
            start.ast(builder, prefix + (isTail ? "    " : "│   "), false);
        } else {
            builder.append(prefix).append(isTail ? "    " : "│   ").append("├── ").append("no start");
        }

        builder.append('\n');
        if (end != null) {
            end.ast(builder, prefix + (isTail ? "    " : "│   "), false);
        } else {
            builder.append(prefix).append(isTail ? "    " : "│   ").append("├── ").append("no end");
        }

        builder.append('\n');
        if (step != null) {
            step.ast(builder, prefix + (isTail ? "    " : "│   "), true);
        } else {
            builder.append(prefix).append(isTail ? "    " : "│   ").append("└── ").append("no step");
        }
    }

}
