package xyz.hexav.aje.types;

import xyz.hexav.aje.AJEException;

public interface OperableValue<T extends OperableValue> {
    static void check(Object a) {
        if(!(a instanceof OperableValue)) {
            throw new AJEException("This operator requires the value to be operable!");
        }
    }
    static void check(Object a, Object b) {
        if(!(a instanceof OperableValue || b instanceof OperableValue)) {
            throw new AJEException("This operator requires both values to be operable!");
        }
    }
    static <T extends OperableValue> OperableValue<T> wrap(T a) {
        return (OperableValue<T>) a;
    }

    double value();

    // Basic arithmetic
    T add(T other);

    T subtract(T other);
    
    T multiply(T other);

    T divide(T other);

    T mod(T other);

    T negative();

    // Boolean logic
    Truth equals(T other);
}
