package xyz.hexav.aje.operators;

import xyz.hexav.aje.types.Expression;

import java.util.function.BinaryOperator;

public interface Operator extends BinaryOperator<Expression> {
    String getSymbol();

    int getArgs();

    boolean isLeftAssoc();
}
