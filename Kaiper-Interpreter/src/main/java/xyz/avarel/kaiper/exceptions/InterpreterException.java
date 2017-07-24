package xyz.avarel.kaiper.exceptions;

import xyz.avarel.kaiper.lexer.Position;

public class InterpreterException extends KaiperException {
    public InterpreterException(String msg) {
        super(msg);
    }

    public InterpreterException(String msg, Position position) {
        this(msg + position);
    }

    public InterpreterException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public InterpreterException(String s, Position position, Throwable throwable) {
        super(s + position, throwable);
    }

    public InterpreterException(Throwable throwable) {
        super(throwable);
    }
}