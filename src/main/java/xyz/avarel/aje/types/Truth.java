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
    public Type<Truth> getType() {
        return TYPE;
    }

    public Truth or(Truth other) {
        if (value) return TRUE;
        if (other.value) return TRUE;
        return FALSE;
    }

    public Truth and(Truth other) {
        if (!value) return FALSE;
        if (!other.value) return FALSE;
        return TRUE;
    }

    @Override
    public Truth negative() {
        return value ? FALSE : TRUE;
    }

    @Override
    public Truth isEqualTo(Truth other) {
        return value == other.value ? TRUE : FALSE;
    }
}
