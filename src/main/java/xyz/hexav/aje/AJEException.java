package xyz.hexav.aje;

public class AJEException extends RuntimeException
{
    public AJEException(String msg) {
        super(msg);
    }

    public AJEException(String msg, Throwable var2) {
        super(msg, var2);
    }

    public AJEException(Throwable var1) {
        super(var1);
    }

    protected AJEException(String msg, Throwable var2, boolean var3, boolean var4) {
        super(msg, var2, var3, var4);
    }
}
