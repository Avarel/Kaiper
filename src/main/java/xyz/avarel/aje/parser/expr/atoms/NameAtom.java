package xyz.avarel.aje.parser.expr.atoms;

import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.runtime.pool.ObjectPool;
import xyz.avarel.aje.runtime.Any;

public class NameAtom implements Expr {
    private final ObjectPool pool;
    private final String name;

    public NameAtom(ObjectPool pool, String name) {
        this.pool = pool;
        this.name = name;
    }

    @Override
    public Any compute() {
        return pool.get(name);
    }

    @Override
    public String toString() {
        return "(name " + name + ")";
    }
}
