package xyz.avarel.aje.types;

public class Type<T> implements Any<Type>, NativeObject<Type> {
    private static final Type<Type> TYPE = new Type<>("type");

    private final Type parent;
    private final T prototype;
    private final String name;
    private final Type[] implicitTypes;

    public Type(String name, Type... implicitTypes) {
        this(null, name, implicitTypes);
    }

    public Type(T prototype, String name, Type... implicitTypes) {
        this(null, prototype, name, implicitTypes);
    }

    public Type(Type parent, T prototype, String name, Type... implicitTypes) {
        this.parent = parent;
        this.prototype = prototype;
        this.name = name;
        this.implicitTypes = implicitTypes;
    }

    @Override
    public Type<Type> getType() {
        return TYPE;
    }

    @Override
    public Type toNative() {
        return this;
    }

    public Type getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public T getPrototype() {
        return prototype;
    }

    @Override
    public String toString() {
        return name;
    }

    public Type[] getImplicitTypes() {
        return implicitTypes;
    }

    public String toExtendedString() {
        if (parent != null) {
            return name + ": " + parent;
        } else {
            return name;
        }
    }
}