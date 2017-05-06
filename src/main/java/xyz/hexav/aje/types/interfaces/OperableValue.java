package xyz.hexav.aje.types.interfaces;

import xyz.hexav.aje.AJEException;
import xyz.hexav.aje.types.Truth;

/**
 * An interface containing all natively implemented operations.
 * @param <T>
 */
public interface OperableValue<T extends OperableValue> extends Value {
    static void assertIs(Object a) {
        if(!(a instanceof OperableValue)) {
            throw new AJEException("This operator requires the value to be operable!");
        }
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

    default T root(T other) {
        throw new UnsupportedOperationException();
    }

    default T negative() {
        throw new UnsupportedOperationException();
    }

    // Boolean logic
    default Truth equals(T other) {
        throw new UnsupportedOperationException();
    }

    default Truth greaterThan(T other) {
        throw new UnsupportedOperationException();
    }

    default Truth lessThan(T other) {
        throw new UnsupportedOperationException();
    }

    default Truth greaterThanOrEqual(T other) {
        throw new UnsupportedOperationException();
    }

    default Truth lessThanOrEqual(T other) {
        throw new UnsupportedOperationException();
    }
}
