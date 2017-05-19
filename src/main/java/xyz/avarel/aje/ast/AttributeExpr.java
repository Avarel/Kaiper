package xyz.avarel.aje.ast;

import xyz.avarel.aje.runtime.Any;

public class AttributeExpr implements Expr {
    private final Expr target;
    private final String name;

    public AttributeExpr(Expr target, String name) {
        this.target = target;
        this.name = name;
    }

    @Override
    public Any compute() {
        return target.compute().identity().get(name);
    }

    @Override
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append("get\n");
        target.ast(builder, prefix + (isTail ? "    " : "│   "), false);
        builder.append('\n');
        builder.append(prefix).append(isTail ? "    " : "│   ").append(name);
    }
}
