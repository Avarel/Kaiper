package xyz.avarel.aje.parser.expr;

import xyz.avarel.aje.runtime.types.Any;

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
}
