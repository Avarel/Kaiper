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

    // Basic arithmetic
    default T add(T other) {
        throw unsupported("addition");
    }

    default T subtract(T other) {
        throw unsupported("subtraction");
    }
    
    default T multiply(T other) {
        throw unsupported("multiplication");
    }

    default T divide(T other) {
        throw unsupported("division");
    }

    default T mod(T other) {
        throw unsupported("modulus");
    }

    default T pow(T other) {
        throw unsupported("exponentiation");
    }

    default T root(T other) {
        throw unsupported("root");
    }

    default T negative() {
        throw unsupported("negation");
    }
    
    default RuntimeException unsupported(String desc) {
        return new AJEException(getType() + " does not support " + desc + ".");
    }

    // Boolean logic
    default Truth equals(T other) {
        throw unsupported("equality comparison");
    }

    default Truth greaterThan(T other) {
        throw unsupported("greater than comparison");
    }

    default Truth lessThan(T other) {
        throw unsupported("less than comparison");
    }

    default Truth greaterThanOrEqual(T other) {
        throw unsupported("greater than or equal to comparison");
    }

    default Truth lessThanOrEqual(T other) {
        throw unsupported("less than or equal to comparison");
    }
}
