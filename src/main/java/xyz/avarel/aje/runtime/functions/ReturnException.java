package xyz.avarel.aje.runtime.functions;

import xyz.avarel.aje.runtime.Obj;

public class ReturnException extends RuntimeException {
    private final Obj value;

    public ReturnException(Obj value) {
        this.value = value;
    }

    public Obj getValue() {
        return value;
    }
}
