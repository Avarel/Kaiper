package xyz.avarel.kaiper.exceptions;

import xyz.avarel.kaiper.lexer.Position;

public class CompilerException extends KaiperException {
    public CompilerException(String msg) {
        super(msg);
    }

    public CompilerException(String msg, Position position) {
        this(msg + position);
    }

    public CompilerException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public CompilerException(String s, Position position, Throwable throwable) {
        super(s + position, throwable);
    }

    public CompilerException(Throwable throwable) {
        super(throwable);
    }
}