package xyz.avarel.aje.parser.expr;

import xyz.avarel.aje.runtime.types.Any;
import xyz.avarel.aje.runtime.types.Undefined;

public enum UndefExpr implements Expr {
    VALUE;

    @Override
    public Any compute() {
        return Undefined.VALUE;
    }
}
