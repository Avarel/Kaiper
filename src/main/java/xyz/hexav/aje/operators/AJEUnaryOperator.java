package xyz.hexav.aje.operators;

import xyz.hexav.aje.types.interfaces.OperableValue;

import java.util.function.UnaryOperator;

public interface AJEUnaryOperator extends UnaryOperator<OperableValue> {
    String getSymbol();
}
