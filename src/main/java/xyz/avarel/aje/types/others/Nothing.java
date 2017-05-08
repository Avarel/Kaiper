package xyz.avarel.aje.types.others;

import xyz.avarel.aje.types.AJEType;
import xyz.avarel.aje.types.AJEObject;
import xyz.avarel.aje.types.NativeObject;

/**
 * Every operation results in the same
 * instance, NOTHING.
 */
public class Nothing implements AJEObject<Nothing>, NativeObject<Void> {
    public static Nothing VALUE = new Nothing();

    public static final AJEType<Nothing> TYPE = new AJEType<>(VALUE, "nothing");

    private Nothing() {}

    @Override
    public String toString() {
        return "Nothing";
    }

    @Override
    public Void toNative() {
        return null;
    }

    @Override
    public AJEType<Nothing> getType() {
        return TYPE;
    }

    @Override
    public Nothing plus(Nothing other) {
        return this;
    }

    @Override
    public Nothing minus(Nothing other) {
        return this;
    }

    @Override
    public Nothing times(Nothing other) {
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
