package xyz.avarel.aje.parser.expr;

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
        return target.compute().get(name);
    }

    @Override
    public void ast(StringBuilder builder, String indent) {
        builder.append(indent).append("get\n");
        target.ast(builder, indent + "│ ");
        builder.append('\n');
        builder.append(indent).append("│ ").append(name);
    }
}
