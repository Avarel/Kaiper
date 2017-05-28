package xyz.avarel.aje;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.ast.Statements;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.lexer.AJELexer;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.functions.ReturnException;
import xyz.avarel.aje.scope.DefaultScope;
import xyz.avarel.aje.scope.Scope;

import java.io.Reader;

public class Expression {
    private final AJEParser parser;
    private final Scope scope;

    private Expr expr;

    public Expression(String script) {
        this(script, DefaultScope.INSTANCE.subPool());
    }

    public Expression(Reader reader) {
        this(reader, DefaultScope.INSTANCE.subPool());
    }

    public Expression(String script, Scope scope) {
        this(new AJELexer(script), scope);
    }

    public Expression(Reader reader, Scope scope) {
        this(new AJELexer(reader), scope);
    }

    public Expression(AJELexer lexer, Scope scope) {
        this.parser = new AJEParser(lexer);
        this.scope = scope;
    }

    public Expression add(String name, Obj object) {
        scope.declare(name, object);
        return this;
    }

    public Expression add(String name, Expression object) {
        scope.declare(name, object.compile().accept(new ExprVisitor(), scope));
        return this;
    }

    public Expr compile() {
        return compile(false);
    }

    public Expr compile(boolean recompile) {
        if (recompile || expr == null) {
            expr = new ExpressionExpr(parser.compile());
        }
        return expr;
    }

    private class ExpressionExpr implements Expr {
        private final Expr expr;

        public ExpressionExpr(Expr expr) {
            this.expr = expr;
        }

        @Override
        public Expr andThen(Expr after) {
            return new ExpressionExpr(new Statements(expr, after));
        }

        @Override
        public Obj accept(ExprVisitor visitor, Scope scope) {
            try {
                return expr.accept(new ExprVisitor(), scope.copy());
            } catch (ReturnException re) {
                return re.getValue();
            }
        }

        @Override
        public Obj compute() {
            return accept(new ExprVisitor(), scope.copy());
        }

        @Override
        public void ast(StringBuilder builder, String indent, boolean isTail) {
            expr.ast(builder, indent, isTail);
        }
    }
}
