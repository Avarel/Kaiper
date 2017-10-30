/*
 *  Copyright 2017 An Tran and Adrian Todt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package xyz.avarel.kaiper.runtime.collections;

import xyz.avarel.kaiper.runtime.Bool;
import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Tuple;
import xyz.avarel.kaiper.runtime.functions.Func;
import xyz.avarel.kaiper.runtime.functions.NativeFunc;
import xyz.avarel.kaiper.runtime.modules.Module;
import xyz.avarel.kaiper.runtime.modules.NativeModule;
import xyz.avarel.kaiper.runtime.numbers.Int;
import xyz.avarel.kaiper.runtime.runtime_pattern.VariableRTPattern;
import xyz.avarel.kaiper.runtime.types.Type;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * Kaiper wrapper class for a one dimensional list.
 */
public class Array implements Obj, Iterable<Obj>, List<Obj> {
    public static final Type<Array> TYPE = new Type<>("Array");
    public static final Module MODULE = new NativeModule("Array") {{
        declare("TYPE", Array.TYPE);

        declare("length", new NativeFunc("length", "array") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                return Int.of(arguments.get("array").as(Array.TYPE).size());
            }
        });

        declare("lastIndex", new NativeFunc("lastIndex", "array") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                return Int.of(arguments.get("array").as(Array.TYPE).size() - 1);
            }
        });

        declare("append", new NativeFunc("append", "array", "elements") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Array array = arguments.get("array").as(Array.TYPE);
                array.list.addAll(arguments.get("elements").as(Array.TYPE).list);
                return array;
            }
        });

        declare("each", new NativeFunc("each", "array", "action") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Func action = arguments.get("action").as(Func.TYPE);

                for (Obj obj : arguments.get("array").as(Array.TYPE)) {
                    action.invoke(new Tuple(obj));
                }
                return Null.VALUE;
            }
        });

        declare("map", new NativeFunc("map", "array", "transform") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Func transform = arguments.get("transform").as(Func.TYPE);

                Array array = new Array();
                for (Obj obj : arguments.get("array").as(Array.TYPE)) {
                    array.add(transform.invoke(new Tuple(obj)));
                }
                return array;
            }
        });

        declare("filter", new NativeFunc("filter", "array", "predicate") {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Func predicate = arguments.get("predicate").as(Func.TYPE);

                Array array = new Array();
                for (Obj obj : arguments.get("array").as(Array.TYPE)) {
                    Bool condition = (Bool) predicate.invoke(new Tuple(obj));
                    if (condition == Bool.TRUE) array.add(obj);
                }
                return array;
            }
        });

        declare("fold",
                new NativeFunc("fold", "array", "accumulator", "operation") {
                    @Override
                    protected Obj eval(Map<String, Obj> arguments) {
                        Obj accumulator = arguments.get("accumulator");
                        Func operation = arguments.get("operation").as(Func.TYPE);

                        for (Obj obj : arguments.get("array").as(Array.TYPE)) {
                            accumulator = operation.invoke(new Tuple(accumulator, obj));
                        }
                        return accumulator;
                    }
                });

        declare("slice", new NativeFunc("slice",
                new VariableRTPattern("self"),
                new VariableRTPattern("start", true),
                new VariableRTPattern("end", true),
                new VariableRTPattern("step", true)
        ) {
            @Override
            protected Obj eval(Map<String, Obj> arguments) {
                Array array = arguments.get("self").as(Array.TYPE);
                Obj startObj = arguments.get("start");
                Obj endObj = arguments.get("end");
                Obj stepObj = arguments.get("step");

                int start, end, step;

                if (startObj == Null.VALUE) {
                    start = 0;
                } else {
                    start = startObj.as(Int.TYPE).value();
                    if (start < 0) start += array.size();
                }

                if (endObj == Null.VALUE) {
                    end = array.size();
                } else {
                    end = endObj.as(Int.TYPE).value();
                    if (end < 0) end += array.size();
                }

                if (stepObj == Null.VALUE) {
                    step = 1;
                } else {
                    step = stepObj.as(Int.TYPE).value();
                }

                if (step == 1) {
                    return Array.ofList(array.subList(Math.max(0, start), Math.min(array.size(), end)));
                } else {
                    if (step > 0) {
                        List<Obj> list = new ArrayList<>();

                        for (int i = start; i < end; i += step) {
                            list.add(array.get(i));
                        }

                        return new Array(list);
                    } else if (step < 0) {
                        List<Obj> list = new ArrayList<>();

                        for (int i = end - 1; i >= start; i += step) {
                            list.add(array.get(i));
                        }

                        return new Array(list);
                    } else { // step == 0
                        return Null.VALUE;
                    }
                }
            }
        });
    }};
    private final List<Obj> list;

    /**
     * Creates an empty array.
     */
    public Array() {
        this(new ArrayList<>());
    }

    public Array(List<Obj> list) {
        this.list = list;
    }

    /**
     * Creates an array of items.
     *
     * @param items {@link Obj} items to put into the list.
     * @return The created {@link Array}.
     */
    public static Array of(Obj... items) {
        Array array = new Array();
        array.list.addAll(Arrays.asList(items));
        return array;
    }

    /**
     * Creates an array of items from a native {@link Collection collection}.
     *
     * @param collection Native {@link Collection collection} of {@link Obj Kaiper objects}.
     * @return The created {@link Array}.
     */
    public static Array ofList(Collection<Obj> collection) {
        Array array = new Array();
        array.list.addAll(collection);
        return array;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
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
    public Type<Array> getType() {
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

    public Obj set(int index, Obj element) {
        if (index < 0) {
            index += size();
        }

        list.set(index, element);
        return element;
    }

    @Override
    public void add(int index, Obj element) {
        list.add(index, element);
    }

    @Override
    public Obj remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<Obj> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<Obj> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<Obj> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    private Obj get(Int index) {
        int i = index.value();

        return get(i);
    }

    public boolean add(Obj obj) {
        return list.add(obj);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Obj> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Obj> c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<Obj> operator) {
        list.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super Obj> c) {
        list.sort(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean equals(Object o) {
        return list.equals(o);
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    @Override
    public Obj get(int index) {
        if (index < 0) {
            index += size();
        }
        if (index < 0 || index >= size()) {
            return Null.VALUE;
        }
        return list.get(index);
    }

    @Override
    public Obj getAttr(String name) {
        switch (name) {
            case "size":
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

    @Override
    public String toString() {
        return list.toString();
    }

    @Override
    public Iterator<Obj> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }
}
