package xyz.avarel.aje.types.others;

import xyz.avarel.aje.AJEException;
import xyz.avarel.aje.operators.AJEBinaryOperator;
import xyz.avarel.aje.types.Type;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.NativeObject;
import xyz.avarel.aje.types.numbers.Int;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;

@SuppressWarnings("unchecked")
public class Slice extends ArrayList<Any> implements Any<Slice>, NativeObject<List<Any>> {
    public static final Slice EMPTY = new Slice();

    public static final Type<Slice> TYPE = new Type<>(EMPTY, "slice");

    public Slice() {
        super();
    }

    public Slice(Any value) {
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

    @Override
    public List<Any> toNative() {
        return Collections.unmodifiableList(this);
    }

    @Override
    public Type<Slice> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public Slice plus(Slice other) {
        return listOperation(Any::plus, other);
    }

    @Override
    public Slice minus(Slice other) {
        return listOperation(Any::minus, other);
    }

    @Override
    public Slice times(Slice other) {
        return listOperation(Any::times, other);
    }

    @Override
    public Slice divide(Slice other) {
        return listOperation(Any::divide, other);
    }

    @Override
    public Slice pow(Slice other) {
        return listOperation(Any::pow, other);
    }

    @Override
    public Slice root(Slice other) {
        return listOperation(Any::root, other);
    }

    @Override
    public Slice negative() {
        return listOperation(Any::negative);
    }


    @Override
    public boolean add(Any Any) {
        if (Any instanceof Slice) { // lets not do the virtual flatmapping again
            return super.addAll((Slice) Any);
        }
        return super.add(Any);
    }

    @Override
    public void add(int index, Any element) {
        if (element instanceof Slice) { // lets not do the virtual flatmapping again
            super.addAll(index, (Slice) element);
            return;
        }
        super.add(index, element);
    }

    public Slice get(Slice indices) {
        Slice slice = new Slice();
        for (Any indice : indices) {
            slice.add(get(((Int) indice).value()));
        }
        return slice;
    }

    @Override
    public Truth equals(Slice other) {
        if (this == other) {
            return Truth.TRUE;
        } else if (size() != other.size()) {
            return Truth.FALSE;
        }

        Slice slice = listOperation(Any::equals, other);
        for (Any o : slice) {
            if (!(o instanceof Truth)) {
                if (o == Truth.FALSE) {
                    return Truth.FALSE;
                }
            }
        }

        return Truth.TRUE;
    }

    private Slice listOperation(AJEBinaryOperator operator, Slice other) {
        int len = size() == 1 ? other.size()
                : other.size() == 1 ? size()
                : Math.min(size(), other.size());
        Slice slice = new Slice();
        for (int i = 0; i < len; i++) {
            slice.add(operator.compile(get(i % size()), other.get(i % other.size())));
        }
        return slice;
    }

    private Slice listOperation(UnaryOperator<Any> operator) {
        Slice slice = new Slice();
        for (int i = 0; i < size(); i++) {
            slice.add(operator.apply(get(i % size())));
        }
        return slice;
    }
}
