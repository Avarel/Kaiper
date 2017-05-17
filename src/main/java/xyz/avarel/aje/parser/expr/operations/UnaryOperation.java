package xyz.avarel.aje.parser.expr.operations;

import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.runtime.Any;

import java.util.function.UnaryOperator;

public class UnaryOperation implements Expr {
    private final Expr left;
    private final UnaryOperator<Any> operator;

    public UnaryOperation(Expr left, UnaryOperator<Any> operator) {
        this.left = left;
        this.operator = operator;
    }

    @Override
    public Any compute() {
        return operator.apply(left.compute());
    }

    @Override
    public void ast(StringBuilder builder, String indent) {
        builder.append(indent).append("unary operation\n");
        left.ast(builder, indent + "│ ");
    }
}
