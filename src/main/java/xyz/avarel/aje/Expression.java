package xyz.avarel.aje;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.lexer.AJELexer;
import xyz.avarel.aje.runtime.Any;
import xyz.avarel.aje.runtime.pool.DefaultPool;
import xyz.avarel.aje.runtime.pool.ObjectPool;

import java.io.Reader;

public class Expression {
    private final AJEParser parser;
    private final ObjectPool pool;

    private Expr expr;

    public Expression(String script) {
        this(script, DefaultPool.INSTANCE.copy());
    }

    public Expression(Reader reader) {
        this(reader, DefaultPool.INSTANCE.copy());
    }

    public Expression(String script, ObjectPool pool) {
        this(new AJELexer(script), pool);
    }

    public Expression(Reader reader, ObjectPool pool) {
        this(new AJELexer(reader), pool);
    }

    public Expression(AJELexer lexer, ObjectPool pool) {
        this.parser = new AJEParser(lexer, pool);
        this.pool = pool;
    }

    public Expression add(String name, Any object) {
        pool.put(name, object);
        return this;
    }

    public Expression add(String name, Expression object) {
        pool.put(name, object.compile().compute());
        return this;
    }

    public Expr compile() {
        return compile(false);
    }

    public Expr compile(boolean recompile) {
        if (recompile || expr == null) {
            expr = parser.compile();
        }
        return expr;
    }
}
