package xyz.avarel.aje.types.others;

import xyz.avarel.aje.AJEException;
import xyz.avarel.aje.operators.ImplicitBinaryOperator;
import xyz.avarel.aje.types.AJEObject;
import xyz.avarel.aje.types.AJEType;
import xyz.avarel.aje.types.NativeObject;
import xyz.avarel.aje.types.numbers.Int;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;

@SuppressWarnings("unchecked")
public class Slice extends ArrayList<AJEObject> implements AJEObject<Slice>, NativeObject<List<AJEObject>> {
    public static final Slice EMPTY = new Slice();

    public static final AJEType<Slice> TYPE = new AJEType<>(EMPTY, "slice");

    public Slice() {
        super();
    }

    public Slice(AJEObject value) {
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
    public List<AJEObject> toNative() {
        return Collections.unmodifiableList(this);
    }

    @Override
    public AJEType<Slice> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public Slice plus(Slice other) {
        return listOperation(AJEObject::plus, other);
    }

    @Override
    public Slice minus(Slice other) {
        return listOperation(AJEObject::minus, other);
    }

    @Override
    public Slice times(Slice other) {
        return listOperation(AJEObject::times, other);
    }

    @Override
    public Slice divide(Slice other) {
        return listOperation(AJEObject::divide, other);
    }

    @Override
    public Slice pow(Slice other) {
        return listOperation(AJEObject::pow, other);
    }

    @Override
    public Slice root(Slice other) {
        return listOperation(AJEObject::root, other);
    }

    @Override
    public Slice negative() {
        return listOperation(AJEObject::negative);
    }


    @Override
    public boolean add(AJEObject AJEObject) {
        if (AJEObject instanceof Slice) { // lets not do the virtual flatmapping again
            return super.addAll((Slice) AJEObject);
        }
        return super.add(AJEObject);
    }

    @Override
    public void add(int index, AJEObject element) {
        if (element instanceof Slice) { // lets not do the virtual flatmapping again
            super.addAll(index, (Slice) element);
            return;
        }
        super.add(index, element);
    }

    public Slice get(Slice indices) {
        Slice slice = new Slice();
        for (AJEObject indice : indices) {
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

        Slice slice = listOperation(AJEObject::equals, other);
        for (AJEObject o : slice) {
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

    private Slice listOperation(UnaryOperator<AJEObject> operator) {
        Slice slice = new Slice();
        for (int i = 0; i < size(); i++) {
            slice.add(operator.apply(get(i % size())));
        }
        return slice;
    }
}
