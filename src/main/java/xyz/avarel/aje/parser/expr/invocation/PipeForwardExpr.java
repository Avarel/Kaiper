package xyz.avarel.aje.parser.expr.invocation;

import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.runtime.types.Any;
import xyz.avarel.aje.runtime.types.Undefined;

public class PipeForwardExpr implements Expr {
    private final Expr left;
    private final Expr right;

    public PipeForwardExpr(Expr left, Expr right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Any compute() {
        // DESUGARING
        if (right instanceof InvocationExpr) {
            InvocationExpr invocation = (InvocationExpr) right;
            invocation.getExprs().add(0, left);
            return invocation.compute();
        }

        return Undefined.VALUE;
    }
}
