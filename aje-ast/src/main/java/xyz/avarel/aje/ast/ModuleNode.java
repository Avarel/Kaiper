package xyz.avarel.aje.ast;

public class ModuleNode implements Single {
    private final String name;
    private final Expr expr;

    public ModuleNode(String name, Expr expr) {
        this.name = name;
        this.expr = expr;
    }

    public String getName() {
        return name;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ")
                .append("module ").append(name);

        builder.append('\n');
        expr.ast(builder, indent + (isTail ? "    " : "│   "), true);
    }
}