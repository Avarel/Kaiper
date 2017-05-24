package xyz.avarel.aje.ast;

import xyz.avarel.aje.ast.atoms.ValueAtom;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.numbers.Int;
import xyz.avarel.aje.runtime.pool.Scope;

public class SublistExpr implements Expr {
    private final Expr left;
    private final Expr start;
    private final Expr endExpr;
    private final Expr step;


    public SublistExpr(Expr left, Expr start, Expr end) {
        this(left, start, end, new ValueAtom(Int.of(1)));
    }

    public SublistExpr(Expr left, Expr start, Expr end, Expr step) {
        this.left = left;
        this.start = start;
        this.endExpr = end;

        this.step = step;
    }

    public Expr getLeft() {
        return left;
    }

    public Expr getStart() {
        return start;
    }

    public Expr getEnd() {
        return endExpr;
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
        start.ast(builder, prefix + (isTail ? "    " : "│   "), false);
        builder.append('\n');
        endExpr.ast(builder, prefix + (isTail ? "    " : "│   "), true);
    }

}
