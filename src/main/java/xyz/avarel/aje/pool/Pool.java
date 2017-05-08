package xyz.avarel.aje.pool;

import xyz.avarel.aje.operators.DefaultOperatorMap;
import xyz.avarel.aje.operators.OperatorMap;
import xyz.avarel.aje.types.AJEObject;

import java.util.HashMap;
import java.util.Map;

public class Pool {
    private final OperatorMap operators;
    private final Map<String, AJEObject> objects;

    public Pool() {
        this(DefaultOperatorMap.INSTANCE.copy(), new HashMap<>());
    }

    public Pool(Pool toCopy) { //TODO FIX HERE
        this(toCopy.operators, toCopy.objects);
    }

    public Pool(OperatorMap operators, Map<String, AJEObject> objects) {
        this.operators = new OperatorMap(operators);
        this.objects = new HashMap<>(objects);
    }

    public void put(String name, AJEObject obj) {
        objects.put(name, obj);
    }

    public AJEObject get(String name) {
        return objects.get(name);
    }

    public OperatorMap getOperators() {
        return operators;
    }

    public Pool copy() {
        return new Pool(this);
    }
}
