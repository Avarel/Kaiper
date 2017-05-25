package xyz.avarel.aje.runtime.lists;

import xyz.avarel.aje.runtime.NativeObject;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Type;
import xyz.avarel.aje.runtime.numbers.Int;

import java.util.ArrayList;
import java.util.List;

public class Range implements Obj, NativeObject<List<Integer>> {
    public static final Type TYPE = new Type("range");
    
    private final int start;
    private final int end;

    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public List<Integer> toNative() {
        List<Integer> list = new ArrayList<>();
        for (int i = start; i <= end; i ++) {
            list.add(i);
        }
        return list;
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    public Vector toVector() {
        Vector vector = new Vector();
        for (int i = start; i <= end; i ++) {
            vector.add(Int.of(i));
        }
        return vector;
    }

    @Override
    public String toString() {
        return start + ".." + end;
    }
}
