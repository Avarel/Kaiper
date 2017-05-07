package xyz.hexav.aje.types.others;

import xyz.hexav.aje.AJEException;
import xyz.hexav.aje.operators.ImplicitBinaryOperator;
import xyz.hexav.aje.types.ImplicitCasts;
import xyz.hexav.aje.types.OperableValue;
import xyz.hexav.aje.types.numbers.Complex;
import xyz.hexav.aje.types.numbers.Decimal;
import xyz.hexav.aje.types.numbers.Int;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;

@SuppressWarnings("unchecked")
public class Slice extends ArrayList<OperableValue> implements OperableValue<Slice>, ImplicitCasts {
    public static final Slice EMPTY = new Slice();

    public Slice() {
        super();
    }

    public Slice(OperableValue value) {
        this();
        add(value);
    }

    public static void assertIs(Object... objs) {
        for (Object a : objs) {
            if(!(a instanceof Slice)) {
                throw new AJEException("Value needs to be a slice.");
            }
        }
    }

    public static Slice range(int start, int end) {
        Slice slice = new Slice();

        if (start < end) {
            for (int i = start; i <= end; i++) {
                slice.add(Decimal.of(i));
            }
        } else {
            for (int i = start; i >= end; i--) {
                slice.add(Decimal.of(i));
            }
        }

        return slice;
    }

    @Override
    public OperableValue[] implicitCastBy(OperableValue target, ImplicitBinaryOperator operator) {
        OperableValue[] objs = new OperableValue[] { this, target };
        objs[0] = this;

        if (target instanceof Decimal
                || target instanceof Int
                || target instanceof Complex) {
            objs[1] = new Slice(target);
        }

        return objs;
    }

    @Override
    public List<OperableValue> toNativeObject() {
        return Collections.unmodifiableList(this);
    }

    @Override
    public String getType() {
        return "slice";
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public Slice add(Slice other) {
        return listOperation(OperableValue::add, other);
    }

    @Override
    public Slice subtract(Slice other) {
        return listOperation(OperableValue::subtract, other);
    }

    @Override
    public Slice multiply(Slice other) {
        return listOperation(OperableValue::multiply, other);
    }

    @Override
    public Slice divide(Slice other) {
        return listOperation(OperableValue::divide, other);
    }

    @Override
    public Slice pow(Slice other) {
        return listOperation(OperableValue::pow, other);
    }

    @Override
    public Slice root(Slice other) {
        return listOperation(OperableValue::root, other);
    }

    @Override
    public Slice negative() {
        return listOperation(OperableValue::negative);
    }

    @Override
    public Truth equals(Slice other) {
        if (this == other) {
            return Truth.TRUE;
        } else if (size() != other.size()) {
            return Truth.FALSE;
        }

        Slice slice = listOperation(OperableValue::equals, other);
        for (OperableValue o : slice) {
            if (!(o instanceof Truth)) {
                if (o == Truth.FALSE) {
                    return Truth.FALSE;
                }
            }
        }

        return Truth.TRUE;
    }

    private Slice listOperation(ImplicitBinaryOperator operator, Slice other) {
        int len = size() == 1 ? other.size()
                : other.size() == 1 ? size()
                : Math.min(size(), other.size());
        Slice slice = new Slice();
        for (int i = 0; i < len; i++) {
            slice.add(operator.compile(get(i % size()), other.get(i % other.size())));
        }
        return slice;
    }

    private Slice listOperation(UnaryOperator<OperableValue> operator) {
        Slice slice = new Slice();
        for (int i = 0; i < size(); i++) {
            slice.add(operator.apply(get(i % size())));
        }
        return slice;
    }
}