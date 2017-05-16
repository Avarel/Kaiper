package xyz.avarel.aje.runtime.types;

public enum Truth implements Any, NativeObject<Boolean> {
    TRUE(true),
    FALSE(false);

    public static final Type<Truth> TYPE = new Type<>("truth");

    private final boolean value;

    Truth(boolean value) {
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
    public Type<Truth> getType() {
        return TYPE;
    }

    @Override
    public Any or(Any other) {
        if (other instanceof Truth) {
            return or((Truth) other);
        }
        return Undefined.VALUE;
    }

    public Truth or(Truth other) {
        if (value) return TRUE;
        if (other.value) return TRUE;
        return FALSE;
    }

    @Override
    public Any and(Any other) {
        if (other instanceof Truth) {
            return and((Truth) other);
        }
        return Undefined.VALUE;
    }

    public Truth and(Truth other) {
        if (!value) return FALSE;
        if (!other.value) return FALSE;
        return TRUE;
    }

    @Override
    public Truth negate() {
        return value ? FALSE : TRUE;
    }

    @Override
    public Truth isEqualTo(Any other) {
        if (other instanceof Truth) {
            return value == ((Truth) other).value ? TRUE : FALSE;
        }
        return FALSE;
    }
}
