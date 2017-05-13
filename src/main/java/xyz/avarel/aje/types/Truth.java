package xyz.avarel.aje.types;

public enum Truth implements Any<Truth>, NativeObject<Boolean> {
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
    public Type getType() {
        return TYPE;
    }

    @Override
    public Truth plus(Truth other) {
        if (value) return TRUE;
        if (other.value) return TRUE;
        return FALSE;
    }

    @Override
    public Truth times(Truth other) {
        if (!value) return FALSE;
        if (!other.value) return FALSE;
        return TRUE;
    }

    @Override
    public Truth negative() {
        return value ? FALSE : TRUE;
    }

    @Override
    public Truth equals(Truth other) {
        return value == other.value ? TRUE : FALSE;
    }
}
