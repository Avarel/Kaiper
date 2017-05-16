package xyz.avarel.aje.parser.expr.invocation;

import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.expr.atoms.NameAtom;
import xyz.avarel.aje.runtime.Any;
import xyz.avarel.aje.runtime.Undefined;

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
        } else if (right instanceof NameAtom) {
            NameAtom name = (NameAtom) right;
            return name.compute().invoke(left.compute());
        }

        return Undefined.VALUE;
    }

    @Override
    public String toString() {
        return "(pipe " + left + ", " + right + ")";
    }
}
