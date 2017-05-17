package xyz.avarel.aje.parser.expr.invocation;

import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.expr.atoms.FunctionAtom;
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
        } else if (right instanceof FunctionAtom) {
            FunctionAtom function = (FunctionAtom) right;
            return function.compute().invoke(left.compute());
        } else if (right instanceof NameAtom) {
            NameAtom name = (NameAtom) right;
            return name.compute().invoke(left.compute());
        }

        return Undefined.VALUE;
    }

    @Override
    public void ast(StringBuilder builder, String indent) {
        builder.append(indent).append("pipe forward\n");
        right.ast(builder, indent + "│ ");
    }
}
