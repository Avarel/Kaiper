package xyz.hexav.aje.operators;

import xyz.hexav.aje.types.OperableValue;

import java.util.function.UnaryOperator;

public interface AJEUnaryOperator extends UnaryOperator<OperableValue> {
    String getSymbol();
}
