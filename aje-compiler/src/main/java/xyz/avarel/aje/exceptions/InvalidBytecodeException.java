package xyz.avarel.aje.exceptions;

public class InvalidBytecodeException extends AJEException {
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
