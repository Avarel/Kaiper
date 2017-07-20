package xyz.avarel.kaiper.exceptions;

public class InvalidBytecodeException extends KaiperException {
    public InvalidBytecodeException(String msg) {
        super(msg);
    }

    public InvalidBytecodeException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public InvalidBytecodeException(Throwable throwable) {
        super(throwable);
    }
}
