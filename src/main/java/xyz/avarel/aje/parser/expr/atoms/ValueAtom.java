package xyz.avarel.aje.parser.expr.atoms;

import xyz.avarel.aje.parser.expr.Expr;
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
    public void ast(StringBuilder builder, String indent) {
        builder.append(indent).append(value);
    }
}
