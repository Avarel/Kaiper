package xyz.avarel.aje.types;

public class AJEType<T> {
    private final AJEType parent;
    private final T prototype;
    private final String name;

    public AJEType(String name) {
        this(null, name);
    }

    public AJEType(T prototype, String name) {
        this(null, prototype, name);
    }

    public AJEType(AJEType parent, T prototype, String name) {
        this.parent = parent;
        this.prototype = prototype;
        this.name = name;
    }

    public AJEType getParent() {
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

    public String toExtendedString() {
        if (parent != null) {
            return name + ": " + parent;
        } else {
            return name;
        }
    }
}