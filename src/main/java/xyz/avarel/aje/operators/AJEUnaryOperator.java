package xyz.avarel.aje.operators;

import xyz.avarel.aje.AJEException;
import xyz.avarel.aje.types.Any;

import java.util.function.UnaryOperator;

public interface AJEUnaryOperator extends UnaryOperator<Any> {
    default String getSymbol() {
        throw new AJEException("Operator did not override getSymbol().");
    }
}
