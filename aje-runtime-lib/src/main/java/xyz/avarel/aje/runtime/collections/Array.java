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
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.functions.Func;
import xyz.avarel.aje.runtime.functions.NativeFunc;
import xyz.avarel.aje.runtime.functions.Parameter;
import xyz.avarel.aje.runtime.numbers.Int;
import xyz.avarel.aje.runtime.types.NativeConstructor;
import xyz.avarel.aje.runtime.types.Type;

import java.util.*;

/**
 * AJE wrapper class for a one dimensional list.
 */
public class Array extends ArrayList<Obj> implements Obj<List<Object>>, Iterable<Obj> {
    public static final Type<Array> TYPE = new ArrayType();

    /**
     * Creates an empty array.
     */
    public Array() {
        super();
    }

    /**
     * Creates an array of items.
     *
     * @param   items
     *          {@link Obj} items to put into the list.
     * @return The created {@link Array}.
     */
    public static Array of(Obj... items) {
        Array array = new Array();
        array.addAll(Arrays.asList(items));
        return array;
    }

    /**
     * Creates an array of items from a native {@link Collection collection}.
     *
     * @param   collection
     *          Native {@link Collection collection} of {@link Obj AJE objects}.
     * @return The created {@link Array}.
     */
    public static Array ofList(Collection<Obj> collection) {
        Array array = new Array();
        array.addAll(collection);
        return array;
    }

    /**
     * Returns an unmodifiable representation of the vector. Note that the list's contents are all converted to
     * their native representation or {@code null} if unable to.
     *
     * @return An unmodifiable representation of the vector.
     */
    @Override
    public List<Object> toJava() {
        List<Object> objects = new ArrayList<>();

        for (Obj obj : this) {
            objects.add(obj.toJava());
        }

        return Collections.unmodifiableList(objects);
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public Bool isEqualTo(Obj other) {
        if (other instanceof Array) {
            return isEqualTo((Array) other);
        }
        return Bool.FALSE;
    }

    public Bool isEqualTo(Array other) {
        if (this == other) {
            return Bool.TRUE;
        } else if (size() != other.size()) {
            return Bool.FALSE;
        }

        return Bool.of(this.equals(other));
    }

    @Override
    public Obj slice(Obj startObj, Obj endObj, Obj stepObj) {
        int start;
        int end;
        int step;

        if (startObj == Undefined.VALUE) {
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

        if (endObj == Undefined.VALUE) {
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

        if (stepObj == Undefined.VALUE) {
            step = 1;
        } else {
            if (stepObj instanceof Int) {
                step = ((Int) stepObj).value();
            } else {
                return Undefined.VALUE;
            }
        }

        if (step == 1) {
            return Array.ofList(subList(Math.max(0, start), Math.min(size(), end)));
        } else {
            if (step > 0) {
                Array newArray = new Array();

                for (int i = start; i < end; i += step) {
                    newArray.add(get(i));
                }

                return newArray;
            } else if (step < 0) {
                Array newArray = new Array();

                for (int i = end - 1; i >= start; i += step) {
                    newArray.add(get(i));
                }

                return newArray;
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
            default:
                return Obj.super.getAttr(name);
        }
    }

    private static class ArrayType extends Type<Array> {
        public ArrayType() {
            super("Array", new NativeConstructor(Parameter.of(Obj.TYPE, true)) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return Array.ofList(arguments);
                }
            });

            getScope().declare("length", new NativeFunc(Parameter.of("this", this)) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return Int.of(((Array) arguments.get(0)).size());
                }
            });

            getScope().declare("size", getScope().lookup("length"));

            getScope().declare("lastIndex", new NativeFunc(Parameter.of("this", this)) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return Int.of(((Array) arguments.get(0)).size() - 1);
                }
            });

            getScope().declare("append", new NativeFunc(Parameter.of(Obj.TYPE, true)) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    ((Array) arguments.get(0)).addAll(arguments);
                    return arguments.get(0);
                }
            });

            getScope().declare("each", new NativeFunc(Parameter.of("this", this), Parameter.of(Func.TYPE)) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    Func action = (Func) arguments.get(1);

                    for (Obj obj : (Array) arguments.get(0)) {
                        action.invoke(Collections.singletonList(obj));
                    }
                    return Undefined.VALUE;
                }
            });

            getScope().declare("map", new NativeFunc(Parameter.of("this", this), Parameter.of(Func.TYPE)) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    Func transform = (Func) arguments.get(1);

                    Array array = new Array();
                    for (Obj obj : (Array) arguments.get(0)) {
                        array.add(transform.invoke(Collections.singletonList(obj)));
                    }
                    return array;
                }
            });

            getScope().declare("filter", new NativeFunc(Parameter.of("this", this), Parameter.of(Func.TYPE)) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    Func predicate = (Func) arguments.get(1);

                    Array array = new Array();
                    for (Obj obj : (Array) arguments.get(0)) {
                        Bool condition = (Bool) predicate.invoke(Collections.singletonList(obj));
                        if (condition == Bool.TRUE) array.add(obj);
                    }
                    return array;
                }
            });

            getScope().declare("fold",
                    new NativeFunc(Parameter.of("this", this), Parameter.of(Obj.TYPE), Parameter.of(Func.TYPE)) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    Obj accumulator = arguments.get(1);
                    Func operation = (Func) arguments.get(2);

                    for (Obj obj : (Array) arguments.get(0)) {
                        accumulator = operation.invoke(accumulator, obj);
                    }
                    return accumulator;
                }
            });
        }
    }
}
