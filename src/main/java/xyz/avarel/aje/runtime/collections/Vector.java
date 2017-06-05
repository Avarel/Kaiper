/*
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package xyz.avarel.aje.runtime.collections;

import xyz.avarel.aje.runtime.Bool;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Type;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.numbers.Int;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

/**
 * AJE wrapper class for a one dimensional vector.
 */
public class Vector extends ArrayList<Obj> implements Obj<List<Object>>, Iterable<Obj> {
    public static final Type<Vector> TYPE = new VectorType();

    /**
     * Creates an empty vector.
     */
    public Vector() {
        super();
    }

    /**
     * Creates a vector of items.
     *
     * @param   items
     *          {@link Obj} items to put into the list.
     * @return  The created {@link Vector}.
     */
    public static Vector of(Obj... items) {
        Vector vector = new Vector();
        vector.addAll(Arrays.asList(items));
        return vector;
    }

    /**
     * Creates a vector of items from a native {@link Collection collection}.
     *
     * @param   collection
     *          Native {@link Collection collection} of {@link Obj AJE objects}.
     * @return  The created {@link Vector}.
     */
    public static Vector ofList(Collection<Obj> collection) {
        Vector vector = new Vector();
        vector.addAll(collection);
        return vector;
    }

    /**
     * Returns an unmodifiable representation of the vector. Note that the list's contents are all converted to
     * their native representation or {@code null} if unable to.
     *
     * @return An unmodifiable representation of the vector.
     */
    @Override
    public List<Object> toNative() {
        List<Object> objects = new ArrayList<>();

        for (Obj obj : this) {
            objects.add(obj.toNative());
        }

        return Collections.unmodifiableList(objects);
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
            vector.add(operator.apply(get(i % size()), other.get(i % other.size())));
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
    public Obj slice(Obj startObj, Obj endObj, Obj stepObj) {
        int start;
        int end;
        int step;

        if (startObj == null) {
            start = 0;
        } else {
            if (startObj instanceof Int) {
                start = ((Int) startObj).value();
                if (start < 0) {
                    start += size();
                }
            } else {
                return Undefined.VALUE;
            }
        }

        if (endObj == null) {
            end = size();
        } else {
            if (endObj instanceof Int) {
                end = ((Int) endObj).value();
                if (end < 0) {
                    end += size();
                }
            } else {
                return Undefined.VALUE;
            }
        }

        if (stepObj == null) {
            step = 1;
        } else {
            if (stepObj instanceof Int) {
                step = ((Int) stepObj).value();
            } else {
                return Undefined.VALUE;
            }
        }

        if (step == 1) {
            return Vector.ofList(subList(start, end));
        } else {
            if (step > 0) {
                Vector newVector = new Vector();

                for (int i = start; i < end; i += step) {
                    newVector.add(get(i));
                }

                return newVector;
            } else if (step < 0) {
                Vector newVector = new Vector();

                for (int i = end - 1; i >= start; i += step) {
                    newVector.add(get(i));
                }

                return newVector;
            } else { // step == 0
                return Undefined.VALUE;
            }
        }
    }

    @Override
    public Obj get(Obj key) {
        if (key instanceof Int) {
            return get((Int) key);
        }
        return Undefined.VALUE;
    }

    @Override
    public Obj set(Obj key, Obj value) {
        if (key instanceof Int) {
            return set((Int) key, value);
        }
        return Undefined.VALUE;
    }

    private Obj set(Int index, Obj element) {
        return set(index.value(), element);
    }

    @Override
    public Obj set(int index, Obj element) {
        if (index < 0) {
            index += size();
        }

        if (index < 0) {
            return Undefined.VALUE;
        } else if (index >= size()) {
            for (int i = size(); i <= index; i++) {
                super.add(Undefined.VALUE);
            }
        }

        super.set(index, element);
        return element;
    }

    private Obj get(Int index) {
        int i = index.value();

        return get(i);
    }

    @Override
    public boolean add(Obj obj) {
        if (obj instanceof Range) {
            return super.addAll(((Range) obj).toVector());
        }
        return super.add(obj);
    }

    @Override
    public Obj get(int index) {
        if (index < 0) {
            index += size();
        }
        if (index < 0 || index >= size()) {
            return Undefined.VALUE;
        }
        return super.get(index);
    }

    @Override
    public Obj getAttr(String name) {
        switch (name) {
            case "size":
                return Int.of(size());
            case "length":
                return Int.of(size());
            case "lastIndex":
                return Int.of(size() - 1);
//            case "append":
//                return new NativeFunction(true, Obj.TYPE) {
//                    @Override
//                    protected Obj eval(Obj target, List<Obj> arguments) {
//                        Vector.this.addAll(arguments);
//                        return Vector.this;
//                    }
//                };
//            case "extend":
//                return new NativeFunction(Vector.TYPE) {
//                    @Override
//                    protected Obj eval(Obj target, List<Obj> arguments) {
//                        Vector.this.addAll(arguments);
//                        return Vector.this;
//                    }
//                };
        }
        return TYPE.getAttr(name);
    }

    public static class VectorType extends Type<Vector> {
        public VectorType() {
            super("Vector");
        }
    }
}
