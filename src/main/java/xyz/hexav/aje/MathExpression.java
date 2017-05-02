package xyz.hexav.aje;

import xyz.hexav.aje.expressions.Expression;
import xyz.hexav.aje.expressions.ExpressionCompiler;
import xyz.hexav.aje.pool.Pool;

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

    public MathExpression setVariable(String name, double[] value) {
        pool.getVar(name).assign(value);
        return this;
    }

    public MathExpression setVariable(String name, Expression value) {
        pool.getVar(name).assign(value);
        return this;
    }

    public double eval() {
        return evalList()[0];
    }

    /**
     * Evaluate and return the result(s) of the function.
     */
    public double[] evalList() {
        compile();
        return expression.evalList();
    }

    public Pool getPool() {
        return pool;
    }
}
