package xyz.avarel.aje.parser.expr;

import xyz.avarel.aje.runtime.types.Any;

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
}