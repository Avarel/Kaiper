package xyz.hexav.aje.types.interfaces;

import xyz.hexav.aje.AJEException;
import xyz.hexav.aje.types.Truth;

public interface ComparableValue<T> extends Value {
    static void assertIs(Object a) {
        if(!(a instanceof ComparableValue)) {
            throw new AJEException("This operator requires both values to be comparable!");
        }
    }

    static <T> ComparableValue<T> wrap(T a) {
        return (ComparableValue<T>) a;
    }

    @Override
    default String getType() {
        return "comparable";
    }

    //Truth equals(T other);

    Truth greaterThan(T other);

    Truth lessThan(T other);

    Truth greaterThanOrEqual(T other);

    Truth lessThanOrEqual(T other);
}
