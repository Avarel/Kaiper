package xyz.avarel.aje.runtime.lists;

import xyz.avarel.aje.runtime.*;
import xyz.avarel.aje.runtime.functions.NativeFunction;
import xyz.avarel.aje.runtime.numbers.Int;
import xyz.avarel.aje.runtime.numbers.Numeric;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class Vector extends ArrayList<Obj> implements Obj, NativeObject<List<Obj>> {
    public static final Type TYPE = new Type("vector");

    public Vector() {
        super();
    }

    public static Vector of(Obj... items) {
        Vector vector = new Vector();
        vector.addAll(Arrays.asList(items));
        return vector;
    }

    public static Vector ofList(Collection<Obj> items) {
        Vector vector = new Vector();
        vector.addAll(items);
        return vector;
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
        if (other instanceof Vector) {
            return plus((Vector) other);
        }
        return plus(Vector.of(other));
    }

    private Vector plus(Vector other) {
        return listOperation(other, Obj::plus);
    }

    @Override
    public Obj minus(Obj other) {
        if (other instanceof Vector) {
            return minus((Vector) other);
        }
        return minus(Vector.of(other));
    }

    private Vector minus(Vector other) {
        return listOperation(other, Obj::minus);
    }

    @Override
    public Obj times(Obj other) {
        if (other instanceof Vector) {
            return times((Vector) other);
        }
        return times(Vector.of(other));
    }

    private Vector times(Vector other) {
        return listOperation(other, Obj::times);
    }

    @Override
    public Obj divide(Obj other) {
        if (other instanceof Vector) {
            return divide((Vector) other);
        }
        return divide(Vector.of(other));
    }

    private Vector divide(Vector other) {
        return listOperation(other, Obj::divide);
    }

    @Override
    public Obj pow(Obj other) {
        if (other instanceof Vector) {
            return pow((Vector) other);
        }
        return pow(Vector.of(other));
    }

    private Vector pow(Vector other) {
        return listOperation(other, Obj::pow);
    }

    @Override
    public Vector negative() {
        return listOperation(Obj::negative);
    }

    @Override
    public Bool isEqualTo(Obj other) {
        if (other instanceof Vector) {
            return isEqualTo((Vector) other);
        }
        return Bool.FALSE;
    }

    public Bool isEqualTo(Vector other) {
        if (this == other) {
            return Bool.TRUE;
        } else if (size() != other.size()) {
            return Bool.FALSE;
        }

        Vector vector = listOperation(other, Obj::isEqualTo);
        for (Obj o : vector) {
            if (!(o instanceof Bool)) {
                if (o == Bool.FALSE) {
                    return Bool.FALSE;
                }
            }
        }

        return Bool.TRUE;
    }

    private Vector listOperation(Vector other, BinaryOperator<Obj> operator) {
        int len = size() == 1 ? other.size()
                : other.size() == 1 ? size()
                : Math.min(size(), other.size());
        Vector vector = Vector.of();
        for (int i = 0; i < len; i++) {
            vector.add(Numeric.process(get(i % size()), other.get(i % other.size()), operator));
        }
        return vector;
    }

    private Vector listOperation(UnaryOperator<Obj> operator) {
        Vector vector = Vector.of();
        for (int i = 0; i < size(); i++) {
            vector.add(operator.apply(get(i % size())));
        }
        return vector;
    }

    @Override
    public Obj get(Obj other) {
        if (other instanceof Int) {
            return get((Int) other);
        }
        return Undefined.VALUE;
    }

    private Obj get(Int index) {
        int i = index.value();
        if (i < 0) {
            i += size();
        }
        return this.get(i);
    }

    @Override
    public Obj getAttr(String name) {
        switch (name) {
            case "size":
                return Int.of(size());
            case "append":
                return new NativeFunction(true, Obj.TYPE) {
                    @Override
                    protected Obj eval(List<Obj> arguments) {
                        Vector.this.addAll(arguments);
                        return Vector.this;
                    }
                };
            case "extend":
                return new NativeFunction(Vector.TYPE) {
                    @Override
                    protected Obj eval(List<Obj> arguments) {
                        Vector.this.addAll(arguments);
                        return Vector.this;
                    }
                };
        }
        return Undefined.VALUE;
    }

    public int lastIndex() {
        return size() - 1;
    }
}
