package xyz.avarel.kaiper.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator<T> implements Iterator<T> {
    private final T[] array;
    private int index = 0;

    public ArrayIterator(T[] array) {
        this.array = array;
    }

    public boolean hasNext() {
        return this.index < this.array.length;
    }

    public T next() {
        if (!hasNext()) throw new NoSuchElementException();
        return this.array[this.index++];
    }
}
