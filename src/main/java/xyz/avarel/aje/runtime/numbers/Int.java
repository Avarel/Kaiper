package xyz.avarel.aje.runtime.numbers;

import xyz.avarel.aje.runtime.*;

public class Int implements Any, NativeObject<Integer> {
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
    public Any plus(Any other) {
        if (other instanceof Int) {
            return this.plus((Int) other);
        } else if (other instanceof Decimal) {
            return Decimal.of(value).plus(other);
        } else if (other instanceof Complex) {
            return Complex.of(value).plus(other);
        }
        return Undefined.VALUE;
    }

    private Int plus(Int other) {
        return Int.of(value + other.value);
    }

    @Override
    public Any minus(Any other) {
        if (other instanceof Int) {
            return this.minus((Int) other);
        } else if (other instanceof Decimal) {
            return Decimal.of(value).minus(other);
        } else if (other instanceof Complex) {
            return Complex.of(value).minus(other);
        }
        return Undefined.VALUE;
    }

    private Int minus(Int other) {
        return Int.of(value - other.value);
    }

    @Override
    public Any times(Any other) {
        if (other instanceof Int) {
            return this.times((Int) other);
        } else if (other instanceof Decimal) {
            return Decimal.of(value).times(other);
        } else if (other instanceof Complex) {
            return Complex.of(value).times(other);
        }
        return Undefined.VALUE;
    }

    private Int times(Int other) {
        return Int.of(value * other.value);
    }

    @Override
    public Any divide(Any other) {
        if (other instanceof Int) {
            return this.divide((Int) other);
        } else if (other instanceof Decimal) {
            return Decimal.of(value).divide(other);
        } else if (other instanceof Complex) {
            return Complex.of(value).divide(other);
        }
        return Undefined.VALUE;
    }

    private Int divide(Int other) {
        return Int.of(value / other.value);
    }

    @Override
    public Any pow(Any other) {
        if (other instanceof Int) {
            return this.pow((Int) other);
        } else if (other instanceof Decimal) {
            return Decimal.of(value).pow(other);
        } else if (other instanceof Complex) {
            return Complex.of(value).pow(other);
        }
        return Undefined.VALUE;
    }

    private Int pow(Int other) {
        return Int.of((int) Math.pow(value, other.value));
    }

    @Override
    public Any mod(Any other) {
        if (other instanceof Int) {
            return this.mod((Int) other);
        } else if (other instanceof Decimal) {
            return Decimal.of(value).mod(other);
        } else if (other instanceof Complex) {
            return Complex.of(value).mod(other);
        }
        return Undefined.VALUE;
    }

    private Int mod(Int other) {
        return Int.of(Math.floorMod(value, other.value));
    }

    @Override
    public Int negative() {
        return Int.of(-value);
    }

    @Override
    public Any isEqualTo(Any other) {
        if (other instanceof Int) {
            return this.isEqualTo((Int) other);
        } else if (other instanceof Decimal) {
            return Decimal.of(value).isEqualTo(other);
        } else if (other instanceof Complex) {
            return Complex.of(value).isEqualTo(other);
        }
        return Truth.FALSE;
    }

    private Any isEqualTo(Int other) {
        return value == other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Any greaterThan(Any other) {
        if (other instanceof Int) {
            return this.greaterThan((Int) other);
        } else if (other instanceof Decimal) {
            return Decimal.of(value).greaterThan(other);
        } else if (other instanceof Complex) {
            return Complex.of(value).greaterThan(other);
        }
        return Truth.FALSE;
    }

    private Any greaterThan(Int other) {
        return value > other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Any lessThan(Any other) {
        if (other instanceof Int) {
            return this.lessThan((Int) other);
        } else if (other instanceof Decimal) {
            return Decimal.of(value).lessThan(other);
        } else if (other instanceof Complex) {
            return Complex.of(value).lessThan(other);
        }
        return Truth.FALSE;
    }

    private Any lessThan(Int other) {
        return value < other.value ? Truth.TRUE : Truth.FALSE;
    }

    @Override
    public Slice rangeTo(Any other) {
        if (other instanceof Int) {
            return this.rangeTo((Int) other);
        }
        return new Slice();
    }

    private Slice rangeTo(Int other) {
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
            for (int k = 0; k < cache.length; k++) {
                cache[k] = new Int(j++);
            }
        }

        private IntCache() {}
    }
}
