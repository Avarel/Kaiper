package xyz.avarel.aje.parser.expr.atoms;

import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.runtime.functions.AJEFunction;

public class FunctionAtom implements Expr {
    private final AJEFunction value;

    public FunctionAtom(AJEFunction value) {
        this.value = value;
    }

    @Override
    public AJEFunction compute() {
        return value;
    }

    @Override
    public void ast(StringBuilder builder, String indent) {
        builder.append(indent).append(value);
    }
}
