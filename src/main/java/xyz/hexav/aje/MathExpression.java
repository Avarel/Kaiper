package xyz.hexav.aje;

import xyz.hexav.aje.expressions.Expression;
import xyz.hexav.aje.pool.Pool;

import java.util.ArrayList;
import java.util.List;

public class MathExpression implements Expression {
    private final List<String> scripts;
    private final List<Expression> expressions;

    private final Pool pool;

    protected MathExpression(List<String> scripts, Pool pool) {
        this.scripts = scripts;
        this.pool = pool;
        this.expressions = new ArrayList<>();
    }

    protected MathExpression(List<String> scripts, List<String> variables, Pool pool) {
        this(scripts, pool);

        if (variables != null) {
            for (String name : variables) {
                getPool().allocVar(name);
            }
        }
    }

    public MathExpression compile() {
        if (expressions.isEmpty()) {
            expressions.addAll(pool.getCompiler().compileScripts(scripts));
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
        double[] value = new double[0];

        compile();

        for (Expression exp : expressions) {
            value = exp.evalList();
            pool.getVar("ans").assign(value);
        }

        return value;
    }

    public Pool getPool() {
        return pool;
    }
}
