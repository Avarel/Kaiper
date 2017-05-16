package xyz.avarel.aje.runtime.types;

public class Type<T> implements Any, NativeObject<Type> {
    private static final Type TYPE = new Type("type");

    private final Type parent;
    private final String name;

    public Type(String name) {
        this(Any.TYPE, name);
    }

    public Type(Type parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public boolean is(Type type) {
        Type t = this;
        do {
            if (t == type) return true;
            t = t.parent;
        } while (t != null);
        return false;
    }

    @Override
    public Type getType() {
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

    @Override
    public String toString() {
        return name;
    }

    public String toExtendedString() {
        if (parent != null) {
            return name + ": " + parent;
        } else {
            return name;
        }
    }
}