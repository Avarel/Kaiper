package xyz.avarel.aje.runtime.types;

import xyz.avarel.aje.AJEException;
import xyz.avarel.aje.runtime.types.numbers.Decimal;

import java.util.Arrays;
import java.util.List;

/**
 * An interface containing all natively implemented operations.
 */
@SuppressWarnings("unchecked")
public interface Any {
    Type<Any> TYPE = new Type("any");

    Type getType();

    default boolean isNativeObject() {
        return this instanceof NativeObject;
    }

    @SuppressWarnings("unchecked")
    default Object toNative() {
        return ((NativeObject) this).toNative();
    }

    // OPERATORS

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

    default Any negate() {
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
    default Any isEqualTo(Any other) {
        return this.equals(other) ? Truth.TRUE : Truth.FALSE;
    }

    default Any greaterThan(Any other) {
        return Undefined.VALUE;
    }

    default Any lessThan(Any other) {
        return Undefined.VALUE;
    }

    default Any or(Any any) {
        return Undefined.VALUE;
    }

    default Any and(Any any) {
        return Undefined.VALUE;
    }


    // Slices
    default Any rangeTo(Any other) {
        return Undefined.VALUE;
    }
//


    // Functional
    default Any invoke(List<Any> args) {
        return Undefined.VALUE;
    }

    default Any invoke(Any... arguments) {
        return invoke(Arrays.asList(arguments));
    }

    default Any identity() {
        return this;
    }

    default Any set(Any other) {
        return Undefined.VALUE;
    }

    default Any get(String name) {
        return Undefined.VALUE;
    }

    default RuntimeException unsupported(String desc) {
        return new AJEException(getType() + " does not support " + desc + ".");
    }
}
