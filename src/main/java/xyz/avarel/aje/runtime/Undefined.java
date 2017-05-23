package xyz.avarel.aje.runtime;

/**
 * Every operation results in the same
 * instance, NOTHING.
 */
public enum Undefined implements Obj, NativeObject<Undefined> {
    VALUE;

    public static final Type<Void> TYPE = new Type<>("undefined");

    @Override
    public String toString() {
        return "undefined";
    }

    @Override
    public Undefined toNative() {
        return this;
    }

    @Override
    public Type getType() {
        return TYPE;
    }
}
