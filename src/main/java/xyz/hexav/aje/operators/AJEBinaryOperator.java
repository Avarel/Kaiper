package xyz.hexav.aje.operators;

import xyz.hexav.aje.types.interfaces.OperableValue;

import java.util.function.BinaryOperator;

public interface AJEBinaryOperator extends BinaryOperator<OperableValue> {
    String getSymbol();
    boolean isLeftAssoc();
}
