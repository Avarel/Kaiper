package xyz.hexav.aje.types.interfaces;

public interface Value {
    default double value() {
        throw new UnsupportedOperationException();
    }
    Object toNativeObject();
    String getType();

    default boolean isComparable() {
        return this instanceof ComparableValue;
    }
    default boolean isOperable() {
        return this instanceof OperableValue;
    }
}
