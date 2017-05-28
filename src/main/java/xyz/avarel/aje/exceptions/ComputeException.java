package xyz.avarel.aje.exceptions;

import xyz.avarel.aje.parser.lexer.Position;

public class ComputeException extends AJEException {
    public ComputeException(String msg) {
        super(msg);
    }

    public ComputeException(String msg, Position position) {
        super(msg + position);
    }

    public ComputeException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ComputeException(String s, Position position, Throwable throwable) {
        super(s + position, throwable);
    }

    public ComputeException(Throwable throwable) {
        super(throwable);
    }
}
