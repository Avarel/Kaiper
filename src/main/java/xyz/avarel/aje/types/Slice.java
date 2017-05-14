package xyz.avarel.aje.types;

import xyz.avarel.aje.functional.AJEFunction;
import xyz.avarel.aje.functional.NativeFunction;
import xyz.avarel.aje.pool.DefaultFunctions;
import xyz.avarel.aje.types.numbers.Int;
import xyz.avarel.aje.types.numbers.Numeric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

@SuppressWarnings("unchecked")
public class Slice extends ArrayList<Any> implements Any<Slice>, NativeObject<List<Any>> {
    public static final Type<Slice> TYPE = new Type<>("slice");

    public Slice() {
        super();
    }


    public Slice(Any value) {
        add(value);
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
    public Slice negative() {
        return listOperation(Any::negative);
    }

    @Override
    public Truth isEqualTo(Slice other) {
        if (this == other) {
            return Truth.TRUE;
        } else if (size() != other.size()) {
            return Truth.FALSE;
        }

        Slice slice = listOperation(Any::isEqualTo, other);
        for (Any o : slice) {
            if (!(o instanceof Truth)) {
                if (o == Truth.FALSE) {
                    return Truth.FALSE;
                }
            }
        }

        return Truth.TRUE;
    }

    private Slice listOperation(BinaryOperator<Any> operator, Slice other) {
        int len = size() == 1 ? other.size()
                : other.size() == 1 ? size()
                : Math.min(size(), other.size());
        Slice slice = new Slice();
        for (int i = 0; i < len; i++) {
            slice.add(Numeric.process(get(i % size()), other.get(i % other.size()), operator));
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

    @Override
    public Any<?> get(String name) {
        switch(name) {
            case "size": return Int.of(size());
            case "map" : return new NativeFunction(AJEFunction.TYPE) {
                @Override
                public Any eval(List<Any> arguments) {
                    AJEFunction transform = (AJEFunction) arguments.get(0);
                    return DefaultFunctions.MAP.get().invoke(Slice.this, transform);
                }
            };
            case "filter" : return new NativeFunction(AJEFunction.TYPE) {
                @Override
                public Any eval(List<Any> arguments) {
                    AJEFunction predicate = (AJEFunction) arguments.get(0);
                    return DefaultFunctions.FILTER.get().invoke(Slice.this, predicate);
                }
            };
            case "fold" : return new NativeFunction(Any.TYPE, AJEFunction.TYPE) {
                @Override
                public Any eval(List<Any> arguments) {
                    Any accumulator = arguments.get(0);
                    AJEFunction operation = (AJEFunction) arguments.get(1);
                    return DefaultFunctions.FOLD.get().invoke(Slice.this, accumulator, operation);
                }
            };
            default: return Undefined.VALUE;
        }
    }
}
