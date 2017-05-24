package xyz.avarel.aje.runtime.lists;

import xyz.avarel.aje.runtime.NativeObject;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Type;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.numbers.Decimal;

import java.util.ArrayList;
import java.util.List;

public class DecimalRange implements Obj, Range, NativeObject<List<Double>> {
    public static final Type TYPE = new Type("range");

    private final double start;
    private final boolean exclusive;
    private final double end;
    private final double step;
    private List<Double> nativeList;

    public DecimalRange(double start, double end, double step, boolean exclusive) {
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
    public List<Double> toNative() {
        // Because all methods are supposed to be pure/functional.
        if (nativeList != null) return nativeList;

        nativeList = new ArrayList<>();
        if (exclusive) {
            for (double i = start; i < end; i += 1) {
                nativeList.add(i * step);
            }
        } else {
            for (double i = start; i <= end; i += 1) {
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
        if (other instanceof Decimal) {
            return plus((Decimal) other);
        }
        return Undefined.VALUE;
    }

    private DecimalRange plus(Decimal other) {
        return new DecimalRange(start + other.value(), end + other.value(), step, exclusive);
    }

    @Override
    public Obj minus(Obj other) {
        if (other instanceof Decimal) {
            return minus((Decimal) other);
        }
        return Undefined.VALUE;
    }

    private DecimalRange minus(Decimal other) {
        return new DecimalRange(start - other.value(), end - other.value(), step, exclusive);
    }

    @Override
    public Obj times(Obj other) {
        if (other instanceof Decimal) {
            return times((Decimal) other);
        }
        return Undefined.VALUE;
    }

    private DecimalRange times(Decimal other) {
        return new DecimalRange(start, end, step * other.value(), exclusive);
    }

    @Override
    public Obj divide(Obj other) {
        if (other instanceof Decimal) {
            return divide((Decimal) other);
        }
        return Undefined.VALUE;
    }

    private DecimalRange divide(Decimal other) {
        return new DecimalRange(start, end, Math.max(step / other.value(), 1), exclusive);
    }

    public Vector toVector() {
        Vector vector = new Vector();
        for (double i : toNative()) {
            vector.add(Decimal.of(i));
        }
        return vector;
    }

    @Override
    public String toString() {
        return toNative().toString();
    }
}
