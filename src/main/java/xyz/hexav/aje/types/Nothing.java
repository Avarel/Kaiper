package xyz.hexav.aje.types;

/**
 * Created by s889542 on 5/3/2017.
 */ /*
 * Every operation results in the same
 * instance, NOTHING.
 */
public class Nothing implements Expression {
    public static Nothing VALUE = new Nothing();

    private Nothing() {}

    @Override
    public double value() {
        return 0;
    }

    public Nothing add(Nothing number) {
        return VALUE;
    }

    public Nothing minus(Nothing number) {
        return VALUE;
    }

    public Nothing times(Nothing number) {
        return VALUE;
    }

    public Nothing divide(Nothing number) {
        return VALUE;
    }

    public Nothing rem(Nothing number) {
        return VALUE;
    }

    public Nothing mod(Nothing number) {
        return VALUE;
    }

    public Nothing isEqualTo(Object obj) {
        return VALUE;
    }

    public Nothing greaterThan(Object obj) {
        return VALUE;
    }

    public Nothing lessThan(Object obj) {
        return VALUE;
    }
}
