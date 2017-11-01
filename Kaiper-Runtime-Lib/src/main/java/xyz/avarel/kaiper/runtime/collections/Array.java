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

import xyz.avarel.kaiper.runtime.IndexedObj;
import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.numbers.Int;
import xyz.avarel.kaiper.runtime.types.Type;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * Kaiper wrapper class for a one dimensional list.
 */
public class Array implements Iterable<Obj>, List<Obj>, IndexedObj {
    public static final Type<Array> TYPE = new Type<>("Array");
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
        if (this == o) return true;
        if (!(o instanceof Array)) return false;

        Array objs = (Array) o;

        return list.equals(objs.list);
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
                return IndexedObj.super.getAttr(name);
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
