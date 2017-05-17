package xyz.avarel.aje.runtime.pool;

import xyz.avarel.aje.runtime.Any;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectPool {
    private final Map<String, Any> pool;
    private final List<ObjectPool> subpools;

    public ObjectPool() {
        pool = new HashMap<>();
        subpools = new ArrayList<>();
    }

    public ObjectPool(ObjectPool copy) {
        pool = new HashMap<>(copy.pool);
        subpools = new ArrayList<>();
    }

    public Any get(String key) {
        if (pool.containsKey(key)) {
            return pool.get(key);
        } else {
            Variable var = new Variable(Undefined.VALUE);
            put(key, var);
            return var;
        }
    }

    public void put(String key, Any value) {
        for (ObjectPool subpool : subpools) {
            if (!subpool.pool.containsKey(key)) {
                subpool.put(key, value);
            }
        }
        pool.put(key, value);
    }

    public ObjectPool copy() {
        return new ObjectPool(this);
    }

    public ObjectPool subPool() {
        ObjectPool subpool = new ObjectPool(this);
        subpools.add(subpool);
        return subpool;
    }

    @Override
    public String toString() {
        return pool.toString();
    }
}
