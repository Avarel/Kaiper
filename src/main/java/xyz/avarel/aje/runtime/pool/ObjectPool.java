package xyz.avarel.aje.runtime.pool;

import xyz.avarel.aje.runtime.Any;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.Variable;

import java.util.HashMap;
import java.util.Map;

public class ObjectPool {
    private final ObjectPool parent;
    private final Map<String, Any> pool;

    public ObjectPool() {
        this(null);
    }

    public ObjectPool(ObjectPool parent) {
        this(parent, new HashMap<>());
    }

    public ObjectPool(ObjectPool parent, Map<String, Any> pool) {
        this.parent = parent;
        this.pool = pool;
    }

    public Any get(String key) {
        if (pool.containsKey(key)) {
            return pool.get(key);
        } else if (parent != null && parent.contains(key)) {
            return parent.get(key);
        } else {
            Variable var = new Variable(Undefined.VALUE);
            put(key, var);
            return var;
        }
    }

    public void put(String key, Any value) {
        pool.put(key, value);
    }

    public boolean contains(String key) {
        return pool.containsKey(key) || parent != null && parent.contains(key);
    }

    public ObjectPool copy() {
        return new ObjectPool(this);
    }

    public ObjectPool subPool() {
        return new ObjectPool(this);
    }

    @Override
    public String toString() {
        return pool.toString();
    }
}
