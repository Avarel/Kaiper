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

package xyz.avarel.kaiper.runtime.collections;

import xyz.avarel.kaiper.runtime.Bool;
import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.functions.Func;
import xyz.avarel.kaiper.runtime.functions.NativeFunc;
import xyz.avarel.kaiper.runtime.functions.Parameter;
import xyz.avarel.kaiper.runtime.modules.Module;
import xyz.avarel.kaiper.runtime.modules.NativeModule;
import xyz.avarel.kaiper.runtime.numbers.Int;
import xyz.avarel.kaiper.runtime.types.Type;

import java.util.*;

/**
 * Kaiper wrapper class for a one dimensional list.
 */
public class Array extends ArrayList<Obj> implements Obj, Iterable<Obj> {
    public static final Type<Array> TYPE = new Type<>("Array");
    public static final Module MODULE = new NativeModule() {{
        declare("TYPE", Array.TYPE);

        declare("length", new NativeFunc("length", "array") {
            @Override
            protected Obj eval(List<Obj> arguments) {
                return Int.of(arguments.get(0).as(Array.TYPE).size());
            }
        });

        declare("lastIndex", new NativeFunc("lastIndex", "array") {
            @Override
            protected Obj eval(List<Obj> arguments) {
                return Int.of(arguments.get(0).as(Array.TYPE).size() - 1);
            }
        });

        declare("append", new NativeFunc("append", Parameter.of("array"), Parameter.of("elements", true)) {
            @Override
            protected Obj eval(List<Obj> arguments) {
                arguments.get(0).as(Array.TYPE).addAll(arguments);
                return arguments.get(0);
            }
        });

        declare("each", new NativeFunc("each", "array", "action") {
            @Override
            protected Obj eval(List<Obj> arguments) {
                Func action = arguments.get(1).as(Func.TYPE);

                for (Obj obj : arguments.get(0).as(Array.TYPE)) {
                    action.invoke(Collections.singletonList(obj));
                }
                return Null.VALUE;
            }
        });

        declare("map", new NativeFunc("map", "array", "transform") {
            @Override
            protected Obj eval(List<Obj> arguments) {
                Func transform = arguments.get(1).as(Func.TYPE);

                Array array = new Array();
                for (Obj obj : arguments.get(0).as(Array.TYPE)) {
                    array.add(transform.invoke(Collections.singletonList(obj)));
                }
                return array;
            }
        });

        declare("filter", new NativeFunc("filter", "array", "predicate") {
            @Override
            protected Obj eval(List<Obj> arguments) {
                Func predicate = arguments.get(1).as(Func.TYPE);

                Array array = new Array();
                for (Obj obj : arguments.get(0).as(Array.TYPE)) {
                    Bool condition = (Bool) predicate.invoke(Collections.singletonList(obj));
                    if (condition == Bool.TRUE) array.add(obj);
                }
                return array;
            }
        });

        declare("fold",
                new NativeFunc("fold", "array", "accumulator", "operation") {
                    @Override
                    protected Obj eval(List<Obj> arguments) {
                        Obj accumulator = arguments.get(1);
                        Func operation = arguments.get(2).as(Func.TYPE);

                        for (Obj obj : arguments.get(0).as(Array.TYPE)) {
                            accumulator = operation.invoke(accumulator, obj);
                        }
                        return accumulator;
                    }
                });
    }};

    /**
     * Creates an empty array.
     */
    public Array() {
        super();
    }

    /**
     * creates an empty array with the specified initial size.
     */
    public Array(int size) {
        super(size);
    }

    /**
     * Creates an array of items.
     *
     * @param   items
     *          {@link Obj} items to put into the list.
     * @return The created {@link Array}.
     */
    public static Array of(Obj... items) {
        Array array = new Array(items.length);
        array.addAll(Arrays.asList(items));
        return array;
    }

    /**
     * Creates an array of items from a native {@link Collection collection}.
     *
     * @param   collection
     *          Native {@link Collection collection} of {@link Obj Kaiper objects}.
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

        if (startObj == Null.VALUE) {
            start = 0;
        } else {
            if (startObj instanceof Int) {
                start = ((Int) startObj).value();
                if (start < 0) {
                    start += size();
                }
            } else {
                return Null.VALUE;
            }
        }

        if (endObj == Null.VALUE) {
            end = size();
        } else {
            if (endObj instanceof Int) {
                end = ((Int) endObj).value();
                if (end < 0) {
                    end += size();
                }
            } else {
                return Null.VALUE;
            }
        }

        if (stepObj == Null.VALUE) {
            step = 1;
        } else {
            if (stepObj instanceof Int) {
                step = ((Int) stepObj).value();
            } else {
                return Null.VALUE;
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
                return Null.VALUE;
            }
        }
    }

    @Override
    public Obj get(Obj key) {
        if (key instanceof Int) {
            return get((Int) key);
        }
        return Null.VALUE;
    }

    @Override
    public Obj set(Obj key, Obj value) {
        if (key instanceof Int) {
            return set((Int) key, value);
        }
        return Null.VALUE;
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
            return Null.VALUE;
        } else if (index >= size()) {
            for (int i = size(); i <= index; i++) {
                super.add(Null.VALUE);
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
            return Null.VALUE;
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

    @Override
    public Obj shl(Obj other) {
        this.add(other);
        return this;
    }
}
