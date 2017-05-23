package xyz.avarel.aje.ast;

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.pool.Scope;

public class AttributeExpr implements Expr {
    private final Expr left;
    private final String name;

    public AttributeExpr(Expr left, String name) {
        this.left = left;
        this.name = name;
    }

    public Expr getLeft() {
        return left;
    }

    public String getName() {
        return name;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append("get\n");
        left.ast(builder, prefix + (isTail ? "    " : "│   "), false);
        builder.append('\n');
        builder.append(prefix).append(isTail ? "    " : "│   ").append("└── ").append(name);
    }
}
