package xyz.hexav.aje.types;

import xyz.hexav.aje.types.interfaces.OperableValue;

/**
 * Every operation results in the same
 * instance, NOTHING.
 */
public class Nothing implements OperableValue<Nothing> {
    public static Nothing VALUE = new Nothing();

    private Nothing() {}

    @Override
    public String toString() {
        return "Nothing";
    }

    @Override
    public Object toNativeObject() {
        return Void.TYPE;
    }

    @Override
    public String getType() {
        return "nothing";
    }

    @Override
    public Nothing add(Nothing other) {
        return this;
    }

    @Override
    public Nothing subtract(Nothing other) {
        return this;
    }

    @Override
    public Nothing multiply(Nothing other) {
        return this;
    }

    @Override
    public Nothing divide(Nothing other) {
        return this;
    }

    @Override
    public Nothing mod(Nothing other) {
        return this;
    }

    @Override
    public Nothing negative() {
        return this;
    }

    @Override
    public Truth equals(Nothing other) {
        return this == other ? Truth.TRUE : Truth.FALSE;
    }
}
