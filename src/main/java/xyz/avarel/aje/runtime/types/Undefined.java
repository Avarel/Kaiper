package xyz.avarel.aje.runtime.types;

/**
 * Every operation results in the same
 * instance, NOTHING.
 */
public enum Undefined implements Any, NativeObject<Undefined> {
    VALUE;

    public static final Type<Void> TYPE = new Type<>( "undefined");

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
