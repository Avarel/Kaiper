package xyz.avarel.aje;

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

    protected AJEException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
