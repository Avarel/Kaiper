package xyz.avarel.aje.runtime;

import xyz.avarel.aje.AJEException;
import xyz.avarel.aje.runtime.numbers.Decimal;

import java.util.Arrays;
import java.util.List;

/**
 * An interface containing all natively implemented operations.
 */
public interface Obj {
    Type TYPE = new Type("any");

    Type getType();

    default boolean isNativeObject() {
        return this instanceof NativeObject;
    }

    default Object toNative() {
        return isNativeObject() ? ((NativeObject<?>) this).toNative() : null;
    }

    // OPERATORS

    // Basic arithmetic
    default Obj plus(Obj other) {
        return Undefined.VALUE;
    }

    default Obj minus(Obj other) {
        return Undefined.VALUE;
    }

    default Obj times(Obj other) {
        return Undefined.VALUE;
    }

    default Obj divide(Obj other) {
        return Undefined.VALUE;
    }

    default Obj mod(Obj other) {
        return Undefined.VALUE;
    }

    default Obj pow(Obj other) {
        return Undefined.VALUE;
    }

    default Obj negative() {
        return Undefined.VALUE;
    }

    default Obj negate() {
        return Undefined.VALUE;
    }

    default Obj plus(double other) {
        return plus(Decimal.of(other));
    }

    default Obj minus(double other) {
        return minus(Decimal.of(other));
    }

    default Obj times(double other) {
        return times(Decimal.of(other));
    }

    default Obj divide(double other) {
        return divide(Decimal.of(other));
    }

    default Obj mod(double other) {
        return mod(Decimal.of(other));
    }

    default Obj pow(double other) {
        return pow(Decimal.of(other));
    }


    // Boolean logic
    default Obj isEqualTo(Obj other) {
        return this.equals(other) ? Bool.TRUE : Bool.FALSE;
    }

    default Obj greaterThan(Obj other) {
        return Undefined.VALUE;
    }

    default Obj lessThan(Obj other) {
        return Undefined.VALUE;
    }

    default Obj or(Obj obj) {
        return Undefined.VALUE;
    }

    default Obj and(Obj obj) {
        return Undefined.VALUE;
    }


    // Slices
    default Obj rangeTo(Obj other, boolean exclusive) {
        return Undefined.VALUE;
    }

    // Functional
    default Obj invoke(List<Obj> args) {
        return Undefined.VALUE;
    }

    default Obj invoke(Obj... arguments) {
        return invoke(Arrays.asList(arguments));
    }

    default Obj identity() {
        return this;
    }

    default Obj set(Obj other) {
        throw new AJEException(getType() + " do not support set operator.");
    }

    default Obj get(Obj other) {
        return Undefined.VALUE;
    }

    default Obj attribute(String name) {
        return Undefined.VALUE;
    }
}
