package xyz.hexav.aje.types;

import xyz.hexav.aje.operators.ImplicitBinaryOperator;

public interface ImplicitCasts {
    OperableValue[] implicitCastBy(OperableValue target, ImplicitBinaryOperator operator);
}
