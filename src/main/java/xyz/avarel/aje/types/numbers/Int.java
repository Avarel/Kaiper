package xyz.avarel.aje.types.numbers;

import xyz.avarel.aje.AJEException;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.Type;
import xyz.avarel.aje.types.NativeObject;
import xyz.avarel.aje.types.others.Slice;
import xyz.avarel.aje.types.others.Truth;

public class Int implements Any<Int>, NativeObject<Integer> {
    public static final Type<Int> TYPE = new Type<>(Int.of(0), "integer", Decimal.TYPE, Complex.TYPE);

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
    public Integer toNative() {
        return value();
    }

    @Override
    public Type<Int> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public Any castUp(Type type) {
        if (type.getPrototype() instanceof Int) {
            return this;
        } else if (type.getPrototype() instanceof Complex) {
            return Complex.of(value);
        } else if (type.getPrototype() instanceof Decimal) {
            return Decimal.of(value);
        }
        return this;
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
