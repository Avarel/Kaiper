package xyz.avarel.aje.ast.variables;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

public class NameAtom implements Expr {
    private final Expr from;
    private final String name;

    public NameAtom(String name) {
        this(null, name);
    }

    public NameAtom(Expr from, String name) {
        this.from = from;
        this.name = name;
    }

    public Expr getFrom() {
        return from;
    }

    public String getName() {
        return name;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        if (from != null) {
            builder.append(indent).append(isTail ? "└── " : "├── ").append("attr\n");
            from.ast(builder, indent + (isTail ? "    " : "│   "), false);
            builder.append('\n');
            builder.append(indent).append(isTail ? "    " : "│   ").append("└── ").append(name);
        } else {
            Expr.super.ast(builder, indent, isTail);
        }
    }
}
