package xyz.avarel.aje.types.others;

import xyz.avarel.aje.types.Type;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.NativeObject;

/**
 * Every operation results in the same
 * instance, NOTHING.
 */
public enum Undefined implements Any<Undefined>, NativeObject<Undefined> {
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
