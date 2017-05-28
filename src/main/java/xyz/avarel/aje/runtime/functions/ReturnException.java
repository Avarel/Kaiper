package xyz.avarel.aje.runtime.functions;

import xyz.avarel.aje.parser.lexer.Position;
import xyz.avarel.aje.runtime.Obj;

public class ReturnException extends RuntimeException {
    private final Position position;
    private final Obj value;

    public ReturnException(Position position, Obj value) {
        this.position = position;
        this.value = value;
    }

    public Position getPosition() {
        return position;
    }

    public Obj getValue() {
        return value;
    }
}
