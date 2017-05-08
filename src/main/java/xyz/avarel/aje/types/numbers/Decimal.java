package xyz.avarel.aje.types.numbers;

import xyz.avarel.aje.AJEException;
import xyz.avarel.aje.types.AJEType;
import xyz.avarel.aje.types.AJEObject;
import xyz.avarel.aje.types.NativeObject;
import xyz.avarel.aje.types.others.Truth;

import java.math.BigDecimal;

public class Decimal implements AJEObject<Decimal>, NativeObject<Double> {
    public static final AJEType<Decimal> TYPE = new AJEType<>(Complex.TYPE, Decimal.of(0), "decimal");

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
    public Double toNative() {
        return value();
    }

    @Override
    public AJEType<Decimal> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public AJEObject castTo(AJEType type) {
        if (type.getPrototype() instanceof Decimal) {
            return this;
        } else if (type.getPrototype() instanceof Complex) {
            return Complex.of(value);
        }
        return this;
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
