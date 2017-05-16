package xyz.avarel.aje.parser.expr.atom;

import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.runtime.types.Any;

public class ValueExpr implements Expr {
    private final Any any;

    public ValueExpr(Any any) {
        this.any = any;
    }

    @Override
    public Any compute() {
        return any;
    }
}
