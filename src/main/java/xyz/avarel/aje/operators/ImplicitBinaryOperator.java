package xyz.avarel.aje.operators;

import xyz.avarel.aje.AJEException;
import xyz.avarel.aje.types.AJEObject;
import xyz.avarel.aje.types.AJEType;

import java.util.function.BinaryOperator;

public interface ImplicitBinaryOperator extends BinaryOperator<AJEObject> {
    default AJEObject compile(AJEObject a, AJEObject b) {
        if (a.getType() == b.getType()) {
            return apply(a, b);
        }

        AJEObject toCast = null;
        AJEType aType = a.getType();
        while (aType != null) {
            aType = aType.getParent();
            if (aType == b.getType()) {
                toCast = a;
            }
        }
        if (toCast == null) {
            AJEType bType = b.getType();
            while (bType != null) {
                bType = bType.getParent();
                if (bType == a.getType()) {
                    toCast = b;
                }
            }
        }

        if (toCast == null) {
            throw new AJEException(a.getType() + " can not interact with " + b.getType() + ".");
        }

        if (toCast == a) {
            a = a.castTo(b.getType());
        } else {
            b = b.castTo(a.getType());
        }

        return apply(a, b);
    }
}
