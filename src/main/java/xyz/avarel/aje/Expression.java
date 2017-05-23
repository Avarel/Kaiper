package xyz.avarel.aje;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.lexer.AJELexer;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.pool.DefaultScope;
import xyz.avarel.aje.runtime.pool.Scope;

import java.io.Reader;

public class Expression {
    private final AJEParser parser;
    private final Scope pool;

    private Expr expr;

    public Expression(String script) {
        this(script, DefaultScope.INSTANCE.subPool());
    }

    public Expression(Reader reader) {
        this(reader, DefaultScope.INSTANCE.subPool());
    }

    public Expression(String script, Scope pool) {
        this(new AJELexer(script), pool);
    }

    public Expression(Reader reader, Scope pool) {
        this(new AJELexer(reader), pool);
    }

    public Expression(AJELexer lexer, Scope pool) {
        this.parser = new AJEParser(lexer, pool);
        this.pool = pool;
    }

    public Expression add(String name, Obj object) {
        pool.assign(name, object);
        return this;
    }

    public Expression add(String name, Expression object) {
        pool.assign(name, object.compile().accept(new ExprVisitor(), pool));
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

    public Obj compute() {
        return compile().accept(new ExprVisitor(), DefaultScope.INSTANCE.subPool());
    }
}
