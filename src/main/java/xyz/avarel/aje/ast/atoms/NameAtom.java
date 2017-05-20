package xyz.avarel.aje.ast.atoms;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.runtime.Any;
import xyz.avarel.aje.runtime.pool.ObjectPool;

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
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append(pool.contains(name) ? pool.get(name) : "var " + name);
    }
}
