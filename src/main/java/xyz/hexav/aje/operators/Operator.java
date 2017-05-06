package xyz.hexav.aje.operators;

import xyz.hexav.aje.types.OperableValue;

import java.util.function.BinaryOperator;

public interface Operator extends BinaryOperator<OperableValue> {
    String getSymbol();

    int getArgs();

    boolean isLeftAssoc();
}
