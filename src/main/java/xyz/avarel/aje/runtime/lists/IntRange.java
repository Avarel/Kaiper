package xyz.avarel.aje.runtime.lists;

import xyz.avarel.aje.runtime.NativeObject;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Type;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.numbers.Int;

import java.util.ArrayList;
import java.util.List;

public class IntRange implements Obj, Range, NativeObject<List<Integer>> {
    public static final Type TYPE = new Type("range");
    
    private final int start;
    private final boolean exclusive;
    private final int end;
    private final int step;
    private List<Integer> nativeList;

    public IntRange(int start, int end, int step, boolean exclusive) {
        this.start = start;
        this.exclusive = exclusive;
        this.end = end;
        this.step = step;
    }

    @Override
    public boolean isExclusive() {
        return exclusive;
    }

    @Override
    public List<Integer> toNative() {
        // Because all methods are supposed to be pure/functional.
        if (nativeList != null) return nativeList;

        nativeList = new ArrayList<>();
        if (exclusive) {
            for (int i = start; i < end; i += 1) {
                nativeList.add(i * step);
            }
        } else {
            for (int i = start; i <= end; i += 1) {
                nativeList.add(i * step);
            }
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
        return new IntRange(start + other.value(), end + other.value(), step, exclusive);
    }

    @Override
    public Obj minus(Obj other) {
        if (other instanceof Int) {
            return minus((Int) other);
        }
        return Undefined.VALUE;
    }

    private IntRange minus(Int other) {
        return new IntRange(start - other.value(), end - other.value(), step, exclusive);
    }

    @Override
    public Obj times(Obj other) {
        if (other instanceof Int) {
            return times((Int) other);
        }
        return Undefined.VALUE;
    }

    private IntRange times(Int other) {
        return new IntRange(start, end, step * other.value(), exclusive);
    }

    @Override
    public Obj divide(Obj other) {
        if (other instanceof Int) {
            return divide((Int) other);
        }
        return Undefined.VALUE;
    }

    private IntRange divide(Int other) {
        return new IntRange(start, end, Math.max(step / other.value(), 1), exclusive);
    }

    public Vector toVector() {
        Vector vector = new Vector();
        for (int i : toNative()) {
            vector.add(Int.of(i));
        }
        return vector;
    }

    @Override
    public String toString() {
        return toNative().toString();
    }
}
