package xyz.avarel.aje.parser.expr.atoms;

import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.runtime.Undefined;

public enum UndefAtom implements Expr {
    VALUE;

    @Override
    public Undefined compute() {
        return Undefined.VALUE;
    }

    @Override
    public void ast(StringBuilder builder, String indent) {
        builder.append(indent).append(Undefined.VALUE);
    }
}
