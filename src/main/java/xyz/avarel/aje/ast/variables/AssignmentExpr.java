package xyz.avarel.aje.ast.variables;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

public class AssignmentExpr implements Expr {
    private final boolean declare;
    private final Expr from;
    private final String name;
    private final Expr expr;

    public AssignmentExpr(String name, Expr expr) {
        this(false, name, expr);
    }

    public AssignmentExpr(boolean declare, String name, Expr expr) {
        this(declare, null, name, expr);
    }

    public AssignmentExpr(Expr from, String name, Expr expr) {
        this(false, from, name, expr);
    }

    public AssignmentExpr(boolean declare, Expr from, String name, Expr expr) {
        this.declare = declare;
        this.from = from;
        this.name = name;
        this.expr = expr;
    }

    public Expr getFrom() {
        return from;
    }

    public String getName() {
        return name;
    }

    public Expr getExpr() {
        return expr;
    }

    public boolean isDeclare() {
        return declare;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append(declare ? "declare\n" : "assign\n");

        if (from != null) {
            from.ast("from", builder, indent + (isTail ? "    " : "│   "), false);
            builder.append('\n');
        }

        builder.append(indent).append(isTail ? "    " : "│   ").append("├── ").append(name);
        builder.append('\n');

        expr.ast("to", builder, indent + (isTail ? "    " : "│   "), true);
    }

    @Override
    public String toString() {
        return "assign";
    }
}
