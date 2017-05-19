package xyz.avarel.aje.ast.atoms;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.runtime.Any;

public class ValueAtom implements Expr {
    private final Any value;

    public ValueAtom(Any value) {
        this.value = value;
    }

    @Override
    public Any compute() {
        return value;
    }

    @Override
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append(value);
    }
}
