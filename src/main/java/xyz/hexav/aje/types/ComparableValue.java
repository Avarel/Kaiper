package xyz.hexav.aje.types;

import xyz.hexav.aje.AJEException;

public interface ComparableValue<T> {
    static void check(Object a, Object b) {
        if(!(a instanceof ComparableValue || b instanceof ComparableValue)) {
            throw new AJEException("This operator requires both values to be comparable!");
        }
    }

    static <T> ComparableValue<T> wrap(T a) {
        return (ComparableValue<T>) a;
    }

    //Truth equals(T other);

    Truth greaterThan(T other);

    Truth lessThan(T other);

    Truth greaterThanOrEqual(T other);

    Truth lessThanOrEqual(T other);
}
