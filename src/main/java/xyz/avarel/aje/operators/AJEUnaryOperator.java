package xyz.avarel.aje.operators;

import xyz.avarel.aje.types.AJEObject;

import java.util.function.UnaryOperator;

public interface AJEUnaryOperator extends UnaryOperator<AJEObject> {
    String getSymbol();
}
