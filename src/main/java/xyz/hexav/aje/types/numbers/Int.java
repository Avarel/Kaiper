package xyz.hexav.aje.types.numbers;

import xyz.hexav.aje.AJEException;
import xyz.hexav.aje.defaults.BinaryOperators;
import xyz.hexav.aje.operators.ImplicitBinaryOperator;
import xyz.hexav.aje.types.others.Slice;
import xyz.hexav.aje.types.others.Truth;
import xyz.hexav.aje.types.ImplicitCasts;
import xyz.hexav.aje.types.OperableValue;

public class Int implements OperableValue<Int>, ImplicitCasts {
    private final int value;

    public Int(int value) {
        this.value = value;
    }

    public static void assertIs(Object... objs) {
        for (Object a : objs) {
            if (!(a instanceof Int)) {
                throw new AJEException("Value needs to be an int.");
            }
        }
    }

    public static Int of(int value) {
        return new Int(value);
    }

    public int value() {
        return value;
    }

    @Override
    public Integer toNativeObject() {
        return value();
    }

    @Override
    public String getType() {
        return "integer";
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public OperableValue[] implicitCastBy(OperableValue target, ImplicitBinaryOperator operator) {
        OperableValue[] objs = new OperableValue[] { this, target };

        if (target instanceof Complex) {
            objs[0] = Complex.of(value);
        } else if (target instanceof Decimal) {
            objs[0] = Decimal.of(value);
        } else if (target instanceof Slice) {
            objs[0] = new Slice(this);
        } else if (target instanceof Int) {
            if (operator == BinaryOperators.EXPONENTATION) {
                if (((Int) target).value < 0) {// If exponentiation is negative, uplift both value to decimal
                    objs[0] = Decimal.of(value);
                    objs[1] = Decimal.of(((Int) target).value);
                }
            } else if (operator == BinaryOperators.DIVIDE) { // uplift both values to decimals
                objs[0] = Decimal.of(value);
                objs[1] = Decimal.of(((Int) target).value);
            }
        }

        return objs;
    }

    @Override
    public Int plus(Int other) {
        return Int.of(value + other.value);
    }

    @Override
    public Int minus(Int other) {
        return Int.of(value - other.value);
    }

    @Override
    public Int times(Int other) {
        return Int.of(value * other.value);
    }

    @Override
    public Int divide(Int other) {
        return Int.of(value / other.value);
    }

    @Override
    public Int pow(Int other) {
        return Int.of((int) Math.pow(value, other.value));
    }

    @Override
    public Int mod(Int other) {
        Int n = Int.of((value % other.value + other.value) % other.value);
        System.out.println(n);
        return n;
    }

    @Override
    public Int negative() {
        return Int.of(-value);
    }

    @Override
    public Truth equals(Int other) {
        return value == other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Truth greaterThan(Int other) {
        return value > other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Truth lessThan(Int other) {
        return value < other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Truth greaterThanOrEqual(Int other) {
        return value >= other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Truth lessThanOrEqual(Int other) {
        return value <= other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Slice rangeTo(Int other) {
        Slice slice = new Slice();

        if (value < other.value) {
            for (int i = value; i <= other.value; i++) {
                slice.add(Int.of(i));
            }
        } else {
            for (int i = value; i >= other.value; i--) {
                slice.add(Int.of(i));
            }
        }

        return slice;
    }
}
