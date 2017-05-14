package xyz.avarel.aje.types.numbers;

import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.NativeObject;
import xyz.avarel.aje.types.Type;
import xyz.avarel.aje.types.Slice;
import xyz.avarel.aje.types.Truth;

public class Int implements Any<Int>, NativeObject<Integer> {
    public static final Type<Int> TYPE = new Type<>(Numeric.TYPE, "integer");

    private final int value;

    private Int(int value) {
        this.value = value;
    }

    public static Int of(int i) {
        if (i >= IntCache.low && i <= IntCache.high) {
            return IntCache.cache[i + (-IntCache.low)];
        }
        return new Int(i);
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
        return Int.of(Math.floorMod(value, other.value));
    }

    @Override
    public Int negative() {
        return Int.of(-value);
    }

    @Override
    public Truth isEqualTo(Int other) {
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

    private static class IntCache {
        static final int low = -128;
        static final int high = 127;
        static final Int[] cache;

        static {
            System.out.println("loading cache");
            cache = new Int[(high - low) + 1];
            int j = low;
            for(int k = 0; k < cache.length; k++)
                cache[k] = new Int(j++);
        }

        private IntCache() {}
    }
}
