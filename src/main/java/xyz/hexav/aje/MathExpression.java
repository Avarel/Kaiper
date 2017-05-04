package xyz.hexav.aje;

import xyz.hexav.aje.expressions.ExpressionCompiler;
import xyz.hexav.aje.pool.Pool;
import xyz.hexav.aje.types.Expression;

import java.util.List;

public class MathExpression implements Expression {
    private final String script;
    private Expression expression;

    private final Pool pool;

    protected MathExpression(String scripts, Pool pool) {
        this.script = scripts;
        this.pool = pool;
    }

    protected MathExpression(String scripts, List<String> variables, Pool pool) {
        this(scripts, pool);

        if (variables != null) {
            for (String name : variables) {
                getPool().allocVar(name);
            }
        }
    }

    public MathExpression compile() {
        if (expression == null) {
            expression = new ExpressionCompiler(pool, script).compile();
        }
        return this;
    }

    public MathExpression setVariable(String name, double value) {
        pool.getVar(name).assign(value);
        return this;
    }

    public MathExpression setVariable(String name, Expression value) {
        pool.getVar(name).assign(value);
        return this;
    }

    public double value() {
        compile();
        return expression.value();
    }

    @Override
    public String asString() {
        value();
        return expression.asString();
    }

    public Expression getExpression() {
        return expression;
    }

    public Pool getPool() {
        return pool;
    }
}
