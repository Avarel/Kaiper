package xyz.avarel.aje.types;

public interface Value<T extends AJEObject> {
    //NATIVE toNativeObject();
    AJEType<T> getType();

    default boolean isNativeObject() {
        return this instanceof NativeObject;
    }

    @SuppressWarnings("unchecked")
    default Object toNative() {
        return ((NativeObject<T>) this).toNative();
    }

    //<T extends AJEObject> AJEObject<T, ?> castTo(AJEType<T> type);
}
