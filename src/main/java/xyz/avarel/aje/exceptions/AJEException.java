package xyz.avarel.aje.exceptions;

public class AJEException extends RuntimeException {
    public AJEException(String msg) {
        super(msg);
    }

    public AJEException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public AJEException(Throwable throwable) {
        super(throwable);
    }
}
