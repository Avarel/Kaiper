package xyz.avarel.aje.parser.expr.atoms;

import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.runtime.types.Truth;

public enum TruthExpr implements Expr {
    TRUE(Truth.TRUE),
    FALSE(Truth.FALSE);

    private final Truth value;

    TruthExpr(Truth value) {
        this.value = value;
    }

    @Override
    public Truth compute() {
        return value;
    }
}
