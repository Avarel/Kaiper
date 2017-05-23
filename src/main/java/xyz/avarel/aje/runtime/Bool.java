package xyz.avarel.aje.runtime;

public enum Bool implements Obj, NativeObject<Boolean> {
    TRUE(true),
    FALSE(false);

    public static final Type TYPE = new Type("boolean");

    private final boolean value;

    Bool(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

    @Override
    public Boolean toNative() {
        return value;
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public Obj or(Obj other) {
        if (other instanceof Bool) {
            return or((Bool) other);
        }
        return Undefined.VALUE;
    }

    public Bool or(Bool other) {
        if (value) return TRUE;
        if (other.value) return TRUE;
        return FALSE;
    }

    @Override
    public Obj and(Obj other) {
        if (other instanceof Bool) {
            return and((Bool) other);
        }
        return Undefined.VALUE;
    }

    public Bool and(Bool other) {
        if (!value) return FALSE;
        if (!other.value) return FALSE;
        return TRUE;
    }

    @Override
    public Bool negate() {
        return value ? FALSE : TRUE;
    }

    @Override
    public Bool isEqualTo(Obj other) {
        if (other instanceof Bool) {
            return value == ((Bool) other).value ? TRUE : FALSE;
        }
        return FALSE;
    }
}
