package xyz.avarel.aje.parser.expr.operations;

import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.runtime.Any;

import java.util.function.BinaryOperator;

public class BinaryOperation implements Expr {
    private final Expr left;
    private final Expr right;
    private final BinaryOperator<Any> operator;
    private final String node;

    public BinaryOperation(Expr left, Expr right, BinaryOperator<Any> operator) {
        this(left, right, operator, "binary");
    }

    public BinaryOperation(Expr left, Expr right, BinaryOperator<Any> operator, String node) {
        this.left = left;
        this.right = right;
        this.operator = operator;
        this.node = node;
    }

    @Override
    public Any compute() {
        return operator.apply(left.compute(), right.compute());
    }

    @Override
    public String toString() {
        return "(" + node + " " + left + ", " + right + ")";
    }
}
