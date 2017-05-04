package xyz.hexav.aje.types;

/**
 * Created by s889542 on 5/3/2017.
 */ /*
 * Every operation results in the same
 * instance, NOTHING.
 */
public class AJENothing implements AJEValue {
    public static AJENothing VALUE = new AJENothing();

    private AJENothing() {}

    @Override
    public double value() {
        return 0;
    }

    public AJENothing add(AJENothing number) {
        return VALUE;
    }

    public AJENothing minus(AJENothing number) {
        return VALUE;
    }

    public AJENothing times(AJENothing number) {
        return VALUE;
    }

    public AJENothing divide(AJENothing number) {
        return VALUE;
    }

    public AJENothing rem(AJENothing number) {
        return VALUE;
    }

    public AJENothing mod(AJENothing number) {
        return VALUE;
    }

    public AJENothing isEqualTo(Object obj) {
        return VALUE;
    }

    public AJENothing greaterThan(Object obj) {
        return VALUE;
    }

    public AJENothing lessThan(Object obj) {
        return VALUE;
    }
}
