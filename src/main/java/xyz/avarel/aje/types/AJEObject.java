package xyz.avarel.aje.types;

import xyz.avarel.aje.AJEException;
import xyz.avarel.aje.types.others.Slice;
import xyz.avarel.aje.types.others.Truth;

import java.util.List;

/**
 * An interface containing all natively implemented operations.
 * @param <T> AJE representation.
 */
public interface AJEObject<T extends AJEObject> extends Value<T> {
    AJEType<AJEObject> TYPE = new AJEType<>("any");

    // Basic arithmetic
    default T plus(T other) {
        throw unsupported("addition");
    }

    default T minus(T other) {
        throw unsupported("subtraction");
    }
    
    default T times(T other) {
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



    // Slices
    default Slice rangeTo(T other) {
        throw unsupported("range to");
    }



    // Functional
    default AJEObject invoke(List<AJEObject> arguments) {
        throw unsupported("invocation");
    }

    default AJEObject castTo(AJEType type) {
        throw unsupported("casting");
    }


    default RuntimeException unsupported(String desc) {
        return new AJEException(getType() + " does not support " + desc + ".");
    }
}
