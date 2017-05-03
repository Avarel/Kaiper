package xyz.hexav.aje.types;

/**
 * Created by s889542 on 5/3/2017.
 */ /*
 * Every operation results in the same
 * instance, NOTHING.
 */
public class AJENothing extends AJENumber {
    public AJENothing VALUE = new AJENothing();

    private AJENothing() {}

    @Override
    public double eval() {
        return 0;
    }

    // TODO remove this
    @Override
    public double[] evalList() {
        return new double[0];
    }

    // TODO convert Expressions into single values.

    public AJENothing add(AJENothing number) {
        return this;
    }

    public AJENothing minus(AJENothing number) {
        return this;
    }

    public AJENothing times(AJENothing number) {
        return this;
    }

    public AJENothing divide(AJENothing number) {
        return this;
    }

    public AJENothing rem(AJENothing number) {
        return this;
    }

    public AJENothing mod(AJENothing number) {
        return this;
    }

    public AJENothing isEqualTo(Object obj) {
        return this;
    }

    public AJENothing greaterThan(Object obj) {
        return this;
    }

    public AJENothing lessThan(Object obj) {
        return this;
    }
}
