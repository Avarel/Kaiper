package xyz.avarel.aje.ast;

import xyz.avarel.aje.runtime.Any;

public class AttributeExpr implements Expr {
    private final Expr left;
    private final String name;

    public AttributeExpr(Expr left, String name) {
        this.left = left;
        this.name = name;
    }

    @Override
    public Any compute() {
        return left.compute().identity().get(name);
    }

    @Override
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append("get\n");
        left.ast(builder, prefix + (isTail ? "    " : "│   "), false);
        builder.append('\n');
        builder.append(prefix).append(isTail ? "    " : "│   ").append("└── ").append(name);
    }
}
