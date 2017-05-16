package xyz.avarel.aje.parser.expr.atoms;

import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.runtime.Truth;

public enum TruthAtom implements Expr {
    TRUE(Truth.TRUE),
    FALSE(Truth.FALSE);

    private final Truth value;

    TruthAtom(Truth value) {
        this.value = value;
    }

    @Override
    public Truth compute() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
