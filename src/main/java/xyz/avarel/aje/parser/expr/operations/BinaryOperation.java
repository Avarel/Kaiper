package xyz.avarel.aje.parser.expr.operations;

import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.runtime.Any;

import java.util.function.BinaryOperator;

public class BinaryOperation implements Expr {
    private final Expr left;
    private final Expr right;
    private final BinaryOperator<Any> operator;

    public BinaryOperation(Expr left, Expr right, BinaryOperator<Any> operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public Any compute() {
        return operator.apply(left.compute().identity(), right.compute().identity());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        ast(builder, "");
        return builder.toString();
    }

    @Override
    public void ast(StringBuilder builder, String indent) {
        builder.append(indent).append("binary operation\n");
        left.ast(builder, indent + "│ ");
        builder.append('\n');
        right.ast(builder, indent + "│ ");
    }
}
