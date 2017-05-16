package xyz.avarel.aje.parser.expr.atom;

import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.runtime.pool.ObjectPool;
import xyz.avarel.aje.runtime.types.Any;

public class NameExpr implements Expr {
    private final ObjectPool pool;
    private final String name;

    public NameExpr(ObjectPool pool, String name) {
        this.pool = pool;
        this.name = name;
    }

    @Override
    public Any compute() {
        return pool.get(name);
    }
}
