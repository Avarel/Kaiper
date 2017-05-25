package xyz.avarel.aje.ast;

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.pool.Scope;

public class AssignmentExpr implements Expr {
    private final String name;
    private final Expr expr;
    private final boolean declare;

    public AssignmentExpr(String name, Expr expr, boolean declare) {
        this.name = name;
        this.expr = expr;
        this.declare = declare;
    }

    public String getName() {
        return name;
    }

    public Expr getExpr() {
        return expr;
    }

    public boolean isDeclaration() {
        return declare;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append("set\n");
        builder.append(prefix).append(isTail ? "    " : "│   ").append("├── ").append(name);
        builder.append('\n');
        expr.ast(builder, prefix + (isTail ? "    " : "│   "), true);
    }
}
