package xyz.hexav.aje.types.interfaces;

public interface Value {
    default double value() {
        throw new UnsupportedOperationException();
    }
    Object toNativeObject();
    String getType();
}
