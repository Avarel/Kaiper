package xyz.avarel.aje.runtime;

import xyz.avarel.aje.runtime.numbers.Int;
import xyz.avarel.aje.runtime.numbers.Numeric;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class Slice extends ArrayList<Any> implements Any, NativeObject<List<Any>> {
    public static final Type<Slice> TYPE = new Type<>("list");

    public Slice() {
        super();
    }

    public static Slice of(Any... items) {
        Slice slice = new Slice();
        slice.addAll(Arrays.asList(items));
        return slice;
    }

    public static Slice ofList(Collection<Any> items) {
        Slice slice = new Slice();
        slice.addAll(items);
        return slice;
    }

    @Override
    public List<Any> toNative() {
        return Collections.unmodifiableList(this);
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public Any plus(Any other) {
        if (other instanceof Slice) {
            return plus((Slice) other);
        }
        return plus(Slice.of(other));
    }

    private Slice plus(Slice other) {
        return listOperation(other, Any::plus);
    }

    @Override
    public Any minus(Any other) {
        if (other instanceof Slice) {
            return minus((Slice) other);
        }
        return minus(Slice.of(other));
    }

    private Slice minus(Slice other) {
        return listOperation(other, Any::minus);
    }

    @Override
    public Any times(Any other) {
        if (other instanceof Slice) {
            return times((Slice) other);
        }
        return times(Slice.of(other));
    }

    private Slice times(Slice other) {
        return listOperation(other, Any::times);
    }

    @Override
    public Any divide(Any other) {
        if (other instanceof Slice) {
            return divide((Slice) other);
        }
        return divide(Slice.of(other));
    }

    private Slice divide(Slice other) {
        return listOperation(other, Any::divide);
    }

    @Override
    public Any pow(Any other) {
        if (other instanceof Slice) {
            return pow((Slice) other);
        }
        return pow(Slice.of(other));
    }

    private Slice pow(Slice other) {
        return listOperation(other, Any::pow);
    }

    @Override
    public Slice negative() {
        return listOperation(Any::negative);
    }

    @Override
    public Truth isEqualTo(Any other) {
        if (other instanceof Slice) {
            return isEqualTo((Slice) other);
        }
        return Truth.FALSE;
    }

    public Truth isEqualTo(Slice other) {
        if (this == other) {
            return Truth.TRUE;
        } else if (size() != other.size()) {
            return Truth.FALSE;
        }

        Slice slice = listOperation(other, Any::isEqualTo);
        for (Any o : slice) {
            if (!(o instanceof Truth)) {
                if (o == Truth.FALSE) {
                    return Truth.FALSE;
                }
            }
        }

        return Truth.TRUE;
    }

    private Slice listOperation(Slice other, BinaryOperator<Any> operator) {
        int len = size() == 1 ? other.size()
                : other.size() == 1 ? size()
                : Math.min(size(), other.size());
        Slice slice = Slice.of();
        for (int i = 0; i < len; i++) {
            slice.add(Numeric.process(get(i % size()), other.get(i % other.size()), operator));
        }
        return slice;
    }

    private Slice listOperation(UnaryOperator<Any> operator) {
        Slice slice = Slice.of();
        for (int i = 0; i < size(); i++) {
            slice.add(operator.apply(get(i % size())));
        }
        return slice;
    }

    @Override
    public Any get(String name) {
        switch (name) {
            case "size":
                return Int.of(size());
            default:
                return Undefined.VALUE;
        }
    }
}
