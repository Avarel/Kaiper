package xyz.avarel.aje.runtime;

import xyz.avarel.aje.runtime.numbers.Int;

import java.util.ArrayList;
import java.util.List;

public class IntRange implements Obj, NativeObject<List<Integer>> {
    public static final Type TYPE = new Type("range");
    
    private final int start;
    private final int end;
    private final int step;
    private List<Integer> nativeList;

    public IntRange(int start, int end, int step) {
        this.start = start;
        this.end = end;
        this.step = step;
    }

    @Override
    public List<Integer> toNative() {
        // Because all methods are supposed to be pure/functional.
        if (nativeList != null) return nativeList;

        nativeList = new ArrayList<>();
        for (int i = start; i <= end; i += step) {
            nativeList.add(i);
        }
        return nativeList;
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public Obj plus(Obj other) {
        if (other instanceof Int) {
            return plus((Int) other);
        }
        return Undefined.VALUE;
    }

    private IntRange plus(Int other) {
        return new IntRange(start + other.value(), end + other.value(), step);
    }

    @Override
    public Obj minus(Obj other) {
        if (other instanceof Int) {
            return minus((Int) other);
        }
        return Undefined.VALUE;
    }

    private IntRange minus(Int other) {
        return new IntRange(start - other.value(), end - other.value(), step);
    }

    @Override
    public Obj times(Obj other) {
        if (other instanceof Int) {
            return times((Int) other);
        }
        return Undefined.VALUE;
    }

    private IntRange times(Int other) {
        return new IntRange(start * other.value(), end * other.value(), step * other.value());
    }

    @Override
    public Obj divide(Obj other) {
        if (other instanceof Int) {
            return divide((Int) other);
        }
        return Undefined.VALUE;
    }

    @Override
    public Obj get(Obj other) {
        if (other instanceof Int) {
            return get((Int) other);
        }
        return Undefined.VALUE;
    }

    private Obj get(Int index) {
        return Int.of(start + step * index.value());
    }

    private IntRange divide(Int other) {
        return new IntRange(start / other.value(), end / other.value(), Math.max(step / other.value(), 1));
    }

    public Slice toSlice() {
        Slice slice = new Slice();
        for (int i : toNative()) {
            slice.add(Int.of(i));
        }
        return slice;
    }

    @Override
    public String toString() {
        return toNative().toString();
    }
}
