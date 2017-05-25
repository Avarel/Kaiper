package xyz.avarel.aje.ast.operations;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

import java.util.function.BinaryOperator;

public class BinaryOperation implements Expr {
    private final Expr left;
    private final Expr right;
    private final BinaryOperator<Obj> operator;

    public BinaryOperation(Expr left, Expr right, BinaryOperator<Obj> operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public Expr getLeft() {
        return left;
    }

    public Expr getRight() {
        return right;
    }

    public BinaryOperator<Obj> getOperator() {
        return operator;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append("binary op\n");
        left.ast(builder, prefix + (isTail ? "    " : "│   "), false);
        builder.append('\n');
        right.ast(builder, prefix + (isTail ? "    " : "│   "), true);
    }

    @Override
    public String toString() {
        return "binary operation";
    }
}
