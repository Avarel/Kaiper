package xyz.hexav.aje.types;

import xyz.hexav.aje.expressions.Expression;

/*
 * Every operation results in a number.
 * Everything must be returned in the form of AJENumber.
 *      Except for eval();
 */
public class AJENumber implements Expression {
    public static final AJENumber NaN = ofValue(Double.NaN);

    public static final AJENumber TRUE = ofValue(1);
    public static final AJENumber FALSE = ofValue(0);

    private final Expression exp;

    public AJENumber() {
        this.exp = Expression.NOTHING;
    }

    public AJENumber(double value) {
        this.exp = ofValue(value);
    }

    public AJENumber(Expression exp) {
        this.exp = exp;
    }

    @Override
    public double eval() {
        return exp.eval();
    }

    // TODO remove this
    @Override
    public double[] evalList() {
        return exp.evalList();
    }

    // TODO convert Expressions into single values.

    public AJENumber add(AJENumber number) {
        return new AJENumber(() -> new double[] { this.eval() + number.eval() });
    }

    public AJENumber minus(AJENumber number) {
        return new AJENumber(() -> new double[] { this.eval() - number.eval() });
    }

    public AJENumber times(AJENumber number) {
        return new AJENumber(() -> new double[] { this.eval() * number.eval() });
    }

    public AJENumber divide(AJENumber number) {
        return new AJENumber(() -> new double[] { this.eval() / number.eval() });
    }

    public AJENumber rem(AJENumber number) {
        return new AJENumber(() -> new double[] { this.eval() % number.eval() });
    }

    public AJENumber mod(AJENumber number) {
        return new AJENumber(() -> {
            double val1 = this.eval();
            double val2 = number.eval();
            return new double[]{ (val1 % val2 + val2) % val2 };
        });
    }

    public AJENumber isEqualTo(Object obj) {
        if (obj instanceof AJENumber) {
            AJENumber number = (AJENumber) obj;
            return new AJENumber(() -> new double[] { this.eval() == number.eval() ? TRUE.eval() : FALSE.eval() });
        } else {
            return AJENumber.FALSE;
        }
    }

    public AJENumber greaterThan(Object obj) {
        if (obj instanceof AJENumber) {
            AJENumber number = (AJENumber) obj;
            return new AJENumber(() -> new double[] { this.eval() > number.eval() ? TRUE.eval() : FALSE.eval() });
        } else {
            return AJENumber.FALSE;
        }
    }

    public AJENumber lessThan(Object obj) {
        if (obj instanceof AJENumber) {
            AJENumber number = (AJENumber) obj;
            return new AJENumber(() -> new double[] { this.eval() < number.eval() ? TRUE.eval() : FALSE.eval() });
        } else {
            return AJENumber.FALSE;
        }
    }

//    public AJENumber invoke() {
//
//    }
//
//    public AJENumber invoke(List<AJENumber> args) {
//        return NOTHING;
//    }









    static AJENumber ofValue(double value) {
        return new AJENumber(() -> new double[] { value });
    }

}
