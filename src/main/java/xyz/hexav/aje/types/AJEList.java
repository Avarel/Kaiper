package xyz.hexav.aje.types;

import xyz.hexav.aje.expressions.Expression;

public class AJEList extends AJENumber {
    static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
    public static final AJEList EMPTY = ofValues(EMPTY_DOUBLE_ARRAY);

    public AJEList(Expression exp) {
        super(exp);
    }

    public AJEList(double value) {
        super(Expression.ofValue(value));
    }

    public AJEList(double... value) {
        super(Expression.ofValues(value));
    }

    @Override
    public AJENumber add(AJENumber number) {
        return super.add(number);
    }

    @Override
    public AJENumber minus(AJENumber number) {
        return super.minus(number);
    }

    @Override
    public AJENumber times(AJENumber number) {
        return super.times(number);
    }

    @Override
    public AJENumber divide(AJENumber number) {
        return super.divide(number);
    }

    @Override
    public AJEList mod(AJENumber number) {
        return new AJEList(0);
    }

    @Override
    public AJEList isEqualTo(Object obj) {
        return EMPTY;
    }

    static AJEList ofValues(double[] values) {
        return new AJEList(() -> values);
    }
}
