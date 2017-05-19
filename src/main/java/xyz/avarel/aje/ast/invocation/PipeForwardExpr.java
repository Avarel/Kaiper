package xyz.avarel.aje.ast.invocation;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.FunctionAtom;
import xyz.avarel.aje.ast.atoms.NameAtom;
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
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append("pipe forward\n");
        right.ast(builder, prefix + (isTail ? "    " : "│   "), true);
    }
}
