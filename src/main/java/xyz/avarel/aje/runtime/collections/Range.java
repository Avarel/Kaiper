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

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Prototype;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.functions.NativeFunc;
import xyz.avarel.aje.runtime.functions.Parameter;
import xyz.avarel.aje.runtime.numbers.Int;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Range implements Obj<List<Integer>>, Iterable<Int> {
    public static final Prototype<Range> PROTOTYPE = new RangePrototype();
    
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
    public Prototype getType() {
        return PROTOTYPE;
    }

    public Vector toVector() {
        Vector vector = new Vector();
        for (Int i : this) {
            vector.add(i);
        }
        return vector;
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
        return Undefined.VALUE;
    }

    public Obj get(Int index) {
        if (index.value() < size()) {
            return Int.of(start + index.value());
        }
        return Undefined.VALUE;
    }

    @Override
    public Iterator<Int> iterator() {
        return new RangeIterator();
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

    private static class RangePrototype extends Prototype<Range> {
        public RangePrototype() {
            super("Range");

            getScope().declare("length", new NativeFunc(Parameter.of("self")) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return Int.of(((Range) arguments.get(0)).size());
                }
            });

            getScope().declare("lastIndex", new NativeFunc(Parameter.of("self")) {
                @Override
                protected Obj eval(List<Obj> arguments) {
                    return Int.of(((Range) arguments.get(0)).size() - 1);
                }
            });
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
