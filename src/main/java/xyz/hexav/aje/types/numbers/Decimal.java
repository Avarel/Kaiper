package xyz.hexav.aje.types.numbers;

import xyz.hexav.aje.AJEException;
import xyz.hexav.aje.operators.ImplicitBinaryOperator;
import xyz.hexav.aje.types.others.Slice;
import xyz.hexav.aje.types.others.Truth;
import xyz.hexav.aje.types.ImplicitCasts;
import xyz.hexav.aje.types.OperableValue;

import java.math.BigDecimal;

public class Decimal implements OperableValue<Decimal>, ImplicitCasts {
    private final double value;

    public Decimal(double value) {
        this.value = value;
    }

    public static void assertIs(Object... objs) {
        for (Object a : objs) {

            if (!(a instanceof Decimal)) {
                throw new AJEException("Value needs to be a number.");
            }
        }
    }

    public static Decimal of(double value) {
        return new Decimal(value);
    }

    public double value() {
        return value;
    }

    @Override
    public Double toNativeObject() {
        return value();
    }

    @Override
    public String getType() {
        return "decimal";
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
        } else if (target instanceof Int) {
            objs[1] = Decimal.of(((Int) target).value());
        } else if (target instanceof Slice) {
            objs[0] = new Slice(this);
        }

        return objs;
    }

    @Override
    public Decimal plus(Decimal other) {
        return Decimal.of(value + other.value);
    }

    @Override
    public Decimal minus(Decimal other) {
        return Decimal.of(value - other.value);
    }

    @Override
    public Decimal times(Decimal other) {
        return Decimal.of(value * other.value);
    }

    @Override
    public Decimal divide(Decimal other) {
        return Decimal.of(value / other.value);
    }

    @Override
    public Decimal pow(Decimal other) {
        return Decimal.of(Math.pow(value, other.value));
    }

    @Override
    public Decimal root(Decimal other) {
        if (value == 1) {
            return other;
        } else if (value == 2) {
            return Decimal.of(Math.sqrt(other.value));
        } else if (value == 3) {
            return Decimal.of(Math.cbrt(other.value));
        } else {
            double result = Math.pow(value, 1.0 / other.value);
            double val = BigDecimal.valueOf(result).setScale(7, BigDecimal.ROUND_HALF_EVEN).doubleValue();
            return Decimal.of(val);
        }
    }

    @Override
    public Decimal mod(Decimal other) {
        Decimal n = Decimal.of((value % other.value + other.value) % other.value);
        System.out.println(n);
        return n;
    }

    @Override
    public Decimal negative() {
        return Decimal.of(-value);
    }

    @Override
    public Truth equals(Decimal other) {
        return value == other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Truth greaterThan(Decimal other) {
        return value > other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Truth lessThan(Decimal other) {
        return value < other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Truth greaterThanOrEqual(Decimal other) {
        return value >= other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Truth lessThanOrEqual(Decimal other) {
        return value <= other.value ? Truth.TRUE : Truth.FALSE;
    }
}
