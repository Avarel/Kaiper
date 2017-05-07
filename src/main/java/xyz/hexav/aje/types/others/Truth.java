package xyz.hexav.aje.types.others;

import xyz.hexav.aje.AJEException;
import xyz.hexav.aje.types.OperableValue;

public enum Truth implements OperableValue<Truth> {
    TRUE(true),
    FALSE(false);

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
    public Boolean toNativeObject() {
        return value;
    }

    @Override
    public String getType() {
        return "boolean";
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
