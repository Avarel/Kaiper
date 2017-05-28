package xyz.avarel.aje.ast.variables;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.parser.lexer.Position;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

public class AssignmentExpr extends Expr {
    private final boolean declare;
    private final Expr from;
    private final String name;
    private final Expr expr;

    public AssignmentExpr(Position position, String name, Expr expr) {
        this(position, false, name, expr);
    }

    public AssignmentExpr(Position position, boolean declare, String name, Expr expr) {
        this(position, declare, null, name, expr);
    }

    public AssignmentExpr(Position position, Expr from, String name, Expr expr) {
        this(position, false, from, name, expr);
    }

    public AssignmentExpr(Position position, boolean declare, Expr from, String name, Expr expr) {
        super(position);
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

        builder.append(indent).append(isTail ? "    " : "│   ").append("├── name: ").append(name);
        builder.append('\n');

        if (from != null) {
            builder.append('\n');
            from.ast("of", builder, indent + (isTail ? "    " : "│   "), false);
        }

        expr.ast(builder, indent + (isTail ? "    " : "│   "), true);
    }

    @Override
    public String toString() {
        return "assign";
    }
}
