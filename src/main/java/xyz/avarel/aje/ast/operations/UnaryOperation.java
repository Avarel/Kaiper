package xyz.avarel.aje.ast.operations;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

import java.util.function.UnaryOperator;

public class UnaryOperation implements Expr {
    private final Expr target;
    private final UnaryOperator<Obj> operator;

    public UnaryOperation(Expr target, UnaryOperator<Obj> operator) {
        this.target = target;
        this.operator = operator;
    }

    public Expr getTarget() {
        return target;
    }

    public UnaryOperator<Obj> getOperator() {
        return operator;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append("unary op\n");
        target.ast(builder, indent + (isTail ? "    " : "│   "), true);
    }

    @Override
    public String toString() {
        return "unary operation";
    }
}
