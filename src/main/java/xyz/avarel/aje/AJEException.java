package xyz.avarel.aje;

public class AJEException extends RuntimeException {
    public AJEException(String msg) {
        super(msg);
    }

    public AJEException(Throwable cause) {
        super(cause);
    }

    public AJEException(String message, Throwable cause) {
        super(message, cause);
    }
}
