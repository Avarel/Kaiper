package xyz.avarel.aje.types.numbers;

import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.NativeObject;
import xyz.avarel.aje.types.Type;
import xyz.avarel.aje.types.Truth;

public class Decimal implements Any<Decimal>, NativeObject<Double> {
    public static final Type<Decimal> TYPE = new Type<>(Numeric.TYPE, "decimal");

    private final double value;

    public Decimal(double value) {
        this.value = value;
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
    public Type getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
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
