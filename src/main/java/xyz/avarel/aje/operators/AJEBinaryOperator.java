package xyz.avarel.aje.operators;

import xyz.avarel.aje.AJEException;
import xyz.avarel.aje.types.Type;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.others.Undefined;

import java.util.function.BinaryOperator;

public interface AJEBinaryOperator extends BinaryOperator<Any> {
    default String getSymbol() {
        throw new AJEException("Operator did not override getSymbol().");
    }

    default boolean isLeftAssoc() {
        return true;
    }

    default Any compile(Any a, Any b) {
        if (a.getType() == b.getType()) {
            return apply(a, b);
        }

        Any toCast = null;
        for (Type type : a.getType().getImplicitTypes()) {
            if (type == b.getType()) {
                toCast = a;
            }
        }
        if (toCast == null) {
            for (Type type : b.getType().getImplicitTypes()) {
                if (type == a.getType()) {
                    toCast = b;
                }
            }
        }

        if (toCast == null) {
            return Undefined.VALUE;
            //throw new AJEException(a.getType() + " can not interact with " + b.getType() + ".");
        }

        if (toCast == a) {
            a = a.castUp(b.getType());
        } else {
            b = b.castUp(a.getType());
        }

        return apply(a, b);
    }
}
