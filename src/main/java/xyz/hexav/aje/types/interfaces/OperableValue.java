package xyz.hexav.aje.types.interfaces;

import xyz.hexav.aje.AJEException;
import xyz.hexav.aje.types.Truth;

public interface OperableValue<T extends OperableValue> extends Value {
    static void assertIs(Object a) {
        if(!(a instanceof OperableValue)) {
            throw new AJEException("This operator requires the value to be operable!");
        }
    }
    static <T extends OperableValue> OperableValue<T> wrap(T a) {
        return (OperableValue<T>) a;
    }

    @Override
    default String getType() {
        return "operable";
    }

    // Basic arithmetic
    default T add(T other) {
        throw new UnsupportedOperationException();
    }

    default T subtract(T other) {
        throw new UnsupportedOperationException();
    }
    
    default T multiply(T other) {
        throw new UnsupportedOperationException();
    }

    default T divide(T other) {
        throw new UnsupportedOperationException();
    }

    default T mod(T other) {
        throw new UnsupportedOperationException();
    }

    default T pow(T other) {
        throw new UnsupportedOperationException();
    }

    default T negative() {
        throw new UnsupportedOperationException();
    }

    // Boolean logic
    Truth equals(T other);
}
