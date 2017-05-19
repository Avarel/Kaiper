package xyz.avarel.aje.ast.atoms;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.runtime.Truth;

public enum BooleanAtom implements Expr {
    TRUE(Truth.TRUE),
    FALSE(Truth.FALSE);

    private final Truth value;

    BooleanAtom(Truth value) {
        this.value = value;
    }

    @Override
    public Truth compute() {
        return value;
    }

    @Override
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append(value);
    }
}
