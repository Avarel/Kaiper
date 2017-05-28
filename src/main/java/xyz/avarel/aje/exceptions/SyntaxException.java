package xyz.avarel.aje.exceptions;

import xyz.avarel.aje.parser.lexer.Position;

public class SyntaxException extends AJEException {
    public SyntaxException(String msg) {
        super(msg);
    }

    public SyntaxException(String msg, Position position) {
        this(msg + position);
    }

    public SyntaxException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public SyntaxException(String s, Position position, Throwable throwable) {
        super(s + position, throwable);
    }

    public SyntaxException(Throwable throwable) {
        super(throwable);
    }
}
