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

import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.numbers.Int;
import xyz.avarel.kaiper.runtime.types.Type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Range implements Iterable<Int>, Obj {
    public static final Type<Range> TYPE = new Type<>("Range");
    private final int start;
    private final int end;

    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public List<Integer> toJava() {
        List<Integer> list = new ArrayList<>();
        for (Int i : this) {
            list.add(i.value());
        }
        return list;
    }

    @Override
    public Type<Range> getType() {
        return TYPE;
    }

    public Array toArray() {
        Array array = new Array();
        for (Int i : this) {
            array.add(i);
        }
        return array;
    }

    @Override
    public String toString() {
        return start + ".." + end;
    }

    @Override
    public Obj get(Obj key) {
        if (key instanceof Int) {
            return get((Int) key);
        }
        return Obj.super.get(key);
    }

    public Obj get(Int index) {
        return get(index.value());
    }

    @Override
    public Iterator<Int> iterator() {
        return new RangeIterator();
    }

    public Obj get(int i) {
        if (i < size()) return Int.of(start + i);
        return Null.VALUE;
    }

    public int size() {
        return end - start + 1;
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

    private final class RangeIterator implements Iterator<Int> {
        private int cursor;

        private RangeIterator() {
            this.cursor = start;
        }

        @Override
        public boolean hasNext() {
            return cursor <= end;
        }

        @Override
        public Int next() {
            return Int.of(cursor++);
        }
    }
}
