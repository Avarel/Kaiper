package xyz.hexav.aje.operators;

import xyz.hexav.aje.AJEException;
import xyz.hexav.aje.types.ImplicitCasts;
import xyz.hexav.aje.types.OperableValue;

import java.util.function.BinaryOperator;

public interface ImplicitBinaryOperator extends BinaryOperator<OperableValue> {
    default OperableValue compile(OperableValue a, OperableValue b) {
        if (a instanceof ImplicitCasts) {
            OperableValue[] i_pass = ((ImplicitCasts) a).implicitCastBy(b, this);
            return apply(i_pass[0], i_pass[1]);
        } else if (a.getClass().isInstance(b)) {
            return apply(a, b);
        } else {
            throw new AJEException(a.getType() + " can not interact with " + b.getType() + ".");
        }
    }
}
