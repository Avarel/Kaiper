package xyz.avarel.aje.runtime.pool;

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;

import java.util.HashMap;
import java.util.Map;

public class Scope {
    private final Scope parent;
    private final Map<String, Obj> map;

    public Scope() {
        this(null);
    }

    public Scope(Scope parent) {
        this(parent, new HashMap<>());
    }

    public Scope(Scope parent, Map<String, Obj> map) {
        this.parent = parent;
        this.map = map;
    }

    public Obj lookup(String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else if (parent != null && parent.contains(key)) {
            return parent.lookup(key);
        }
        return Undefined.VALUE;
    }

    public void assign(String key, Obj value) {
        assign(key, value, true);
    }

    public void assign(String key, Obj value, boolean declare) { // x = 0; [0..<9] |> each(func(it) { x = x + it }); x
        if (!declare && parent != null) {
            if (parent.contains(key)) {
                parent.assign(key, value, false);
                return;
            }
        }
        map.put(key, value);
    }

    public boolean contains(String key) {
        return map.containsKey(key) || parent != null && parent.contains(key);
    }

    public Scope copy() {
        return new Scope(this.parent, new HashMap<>(map));
    }

    public Scope subPool() {
        return new Scope(this);
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
