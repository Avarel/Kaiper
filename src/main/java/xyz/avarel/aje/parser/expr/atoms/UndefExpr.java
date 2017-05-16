package xyz.avarel.aje.parser.expr.atoms;

import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.runtime.types.Undefined;

public enum UndefExpr implements Expr {
    VALUE;

    @Override
    public Undefined compute() {
        return Undefined.VALUE;
    }
}
