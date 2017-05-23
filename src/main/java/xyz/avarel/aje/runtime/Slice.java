package xyz.avarel.aje.runtime;

import xyz.avarel.aje.runtime.numbers.Int;
import xyz.avarel.aje.runtime.numbers.Numeric;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class Slice extends ArrayList<Obj> implements Obj, NativeObject<List<Obj>> {
    public static final Type TYPE = new Type("list");

    public Slice() {
        super();
    }

    public static Slice of(Obj... items) {
        Slice slice = new Slice();
        slice.addAll(Arrays.asList(items));
        return slice;
    }

    public static Slice ofList(Collection<Obj> items) {
        Slice slice = new Slice();
        slice.addAll(items);
        return slice;
    }

    @Override
    public List<Obj> toNative() {
        return Collections.unmodifiableList(this);
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public Obj plus(Obj other) {
        if (other instanceof Slice) {
            return plus((Slice) other);
        }
        return plus(Slice.of(other));
    }

    private Slice plus(Slice other) {
        return listOperation(other, Obj::plus);
    }

    @Override
    public Obj minus(Obj other) {
        if (other instanceof Slice) {
            return minus((Slice) other);
        }
        return minus(Slice.of(other));
    }

    private Slice minus(Slice other) {
        return listOperation(other, Obj::minus);
    }

    @Override
    public Obj times(Obj other) {
        if (other instanceof Slice) {
            return times((Slice) other);
        }
        return times(Slice.of(other));
    }

    private Slice times(Slice other) {
        return listOperation(other, Obj::times);
    }

    @Override
    public Obj divide(Obj other) {
        if (other instanceof Slice) {
            return divide((Slice) other);
        }
        return divide(Slice.of(other));
    }

    private Slice divide(Slice other) {
        return listOperation(other, Obj::divide);
    }

    @Override
    public Obj pow(Obj other) {
        if (other instanceof Slice) {
            return pow((Slice) other);
        }
        return pow(Slice.of(other));
    }

    private Slice pow(Slice other) {
        return listOperation(other, Obj::pow);
    }

    @Override
    public Slice negative() {
        return listOperation(Obj::negative);
    }

    @Override
    public Bool isEqualTo(Obj other) {
        if (other instanceof Slice) {
            return isEqualTo((Slice) other);
        }
        return Bool.FALSE;
    }

    public Bool isEqualTo(Slice other) {
        if (this == other) {
            return Bool.TRUE;
        } else if (size() != other.size()) {
            return Bool.FALSE;
        }

        Slice slice = listOperation(other, Obj::isEqualTo);
        for (Obj o : slice) {
            if (!(o instanceof Bool)) {
                if (o == Bool.FALSE) {
                    return Bool.FALSE;
                }
            }
        }

        return Bool.TRUE;
    }

    private Slice listOperation(Slice other, BinaryOperator<Obj> operator) {
        int len = size() == 1 ? other.size()
                : other.size() == 1 ? size()
                : Math.min(size(), other.size());
        Slice slice = Slice.of();
        for (int i = 0; i < len; i++) {
            slice.add(Numeric.process(get(i % size()), other.get(i % other.size()), operator));
        }
        return slice;
    }

    private Slice listOperation(UnaryOperator<Obj> operator) {
        Slice slice = Slice.of();
        for (int i = 0; i < size(); i++) {
            slice.add(operator.apply(get(i % size())));
        }
        return slice;
    }

    @Override
    public Obj attribute(String name) {
        switch (name) {
            case "size":
                return Int.of(size());
            default:
                return Undefined.VALUE;
        }
    }
}
