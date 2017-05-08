package xyz.avarel.aje.types.others;

import xyz.avarel.aje.AJEException;
import xyz.avarel.aje.types.AJEType;
import xyz.avarel.aje.types.AJEObject;
import xyz.avarel.aje.types.NativeObject;

public enum Truth implements AJEObject<Truth>, NativeObject<Boolean> {
    TRUE(true),
    FALSE(false);

    public static final AJEType<Truth> TYPE = new AJEType<>(FALSE, "truth");

    private final boolean value;

    Truth(boolean value) {
        this.value = value;
    }

    public static void assertNot(Object... objs) {
        for (Object a : objs) {
            if(a instanceof Truth) {
                throw new AJEException("Value can not be a boolean.");
            }
        }
    }

    public static void assertIs(Object... objs) {
        for (Object a : objs) {
            if(!(a instanceof Truth)) {
                throw new AJEException("Value needs to be a boolean.");
            }
        }
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
    public AJEType<Truth> getType() {
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
