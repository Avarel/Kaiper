package xyz.hexav.aje.types;

import xyz.hexav.aje.AJEException;

public enum Truth implements OperableValue<Truth> {
    TRUE((byte) 1),
    FALSE((byte) 0);

    private final byte value;

    Truth(byte value) {
        this.value = value;
    }

    public static void check(Object a, Object b) {
        if(!(a instanceof Truth || b instanceof Truth)) {
            throw new AJEException("This operator requires both values to be booleans");
        }
    }

    @Override
    public double value() {
        return value;
    }

    @Override
    public String toString() {
        return this == TRUE ? "true" : "false";
    }

    @Override
    public Truth add(Truth other) {
        if (this == TRUE) return TRUE;
        if (other == TRUE) return TRUE;
        return FALSE;
    }

    @Override
    public Truth subtract(Truth other) {
        throw new AJEException("Boolean algebra does not have negative quantities.");
    }

    @Override
    public Truth multiply(Truth other) {
        if (this == FALSE) return FALSE;
        if (other == FALSE) return FALSE;
        return TRUE;
    }

    @Override
    public Truth divide(Truth other) {
        throw new AJEException("Boolean algebra does not have division.");
    }

    @Override
    public Truth mod(Truth other) {
        throw new AJEException("Boolean algebra does not have modulus.");
    }

    @Override
    public Truth negative() {
        return this == TRUE ? FALSE : TRUE;
    }

    @Override
    public Truth equals(Truth other) {
        return this == other ? TRUE : FALSE;
    }
}
