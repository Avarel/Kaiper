package xyz.avarel.aje;

import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.parser.lexer.AJELexer;
import xyz.avarel.aje.runtime.pool.DefaultPool;
import xyz.avarel.aje.runtime.pool.ObjectPool;
import xyz.avarel.aje.runtime.types.Any;

public class Expression {
    private final String expression;
    private final ObjectPool pool;

    private Expr expr;

    public Expression(String expression) {
        this(expression, DefaultPool.INSTANCE.copy());
    }

    public Expression(String expression, ObjectPool pool) {
        this.expression = expression;
        this.pool = pool;
    }

    public Expression add(String name, Any object) {
        pool.put(name, object);
        return this;
    }

    public Expression add(String name, Expression object) {
        pool.put(name, object.compute());
        return this;
    }

    public Any compute() {
        return compute(false);
    }

    public Any compute(boolean recompile) {
        if (recompile || expr == null) {
            AJEParser parser = new AJEParser(new AJELexer(expression), pool);
            expr = parser.compile();
        }
        return expr.compute();
    }
}
