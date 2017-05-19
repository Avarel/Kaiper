package xyz.avarel.aje.ast.atoms;

import xyz.avarel.aje.ast.Expr;
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
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append(value);
    }
}
