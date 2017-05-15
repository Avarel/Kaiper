package xyz.avarel.aje.runtime.types;

import xyz.avarel.aje.AJEException;
import xyz.avarel.aje.runtime.types.numbers.Decimal;

import java.util.Arrays;
import java.util.List;

/**
 * An interface containing all natively implemented operations.
 * @param <T> AJE representation.
 */
@SuppressWarnings("unchecked")
public interface Any<T extends Any> {
    Type<Any> TYPE = new Type("any");

    Type<T> getType();

    default boolean isNativeObject() {
        return this instanceof NativeObject;
    }

    @SuppressWarnings("unchecked")
    default Object toNative() {
        return ((NativeObject<T>) this).toNative();
    }



    // Basic arithmetic
    default Any plus(Any other) {
        return Undefined.VALUE;
    }

    default Any minus(Any other) {
        return Undefined.VALUE;
    }

    default Any times(Any other) {
        return Undefined.VALUE;
    }

    default Any divide(Any other) {
        return Undefined.VALUE;
    }

    default Any mod(Any other) {
        return Undefined.VALUE;
    }

    default Any pow(Any other) {
        return Undefined.VALUE;
    }

    default Any negative() {
        return Undefined.VALUE;
    }


    default Any plus(double other) {
        return plus(Decimal.of(other));
    }

    default Any minus(double other) {
        return minus(Decimal.of(other));
    }

    default Any times(double other) {
        return times(Decimal.of(other));
    }

    default Any divide(double other) {
        return divide(Decimal.of(other));
    }

    default Any mod(double other) {
        return mod(Decimal.of(other));
    }

    default Any pow(double other) {
        return pow(Decimal.of(other));
    }


    // Boolean logic
    default Truth isEqualTo(Any other) {
        return this.equals(other) ? Truth.TRUE : Truth.FALSE;
    }

    default Truth greaterThan(Any other) {
        throw unsupported("greater than comparison");
    }

    default Truth lessThan(Any other) {
        throw unsupported("less than comparison");
    }


    // Slices
    default Slice rangeTo(Any other) {
        throw unsupported("range to");
    }



    // Functional
    default Any invoke(List<Any> args) {
        return Undefined.VALUE;
    }

    default Any invoke(Any... arguments) {
        return invoke(Arrays.asList(arguments));
    }

    default T identity() {
        return (T) this;
    }

    default Any set(Any other) {
        throw unsupported("set");
    }

    default Any get(String name) {
        return Undefined.VALUE;
    }

    default RuntimeException unsupported(String desc) {
        return new AJEException(getType() + " does not support " + desc + ".");
    }
}
