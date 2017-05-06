package xyz.hexav.aje;

import xyz.hexav.aje.pool.Pool;
import xyz.hexav.aje.types.interfaces.Value;

import java.util.List;

public class MathExpression {
    private final String script;
    private Value expression;

    private final Pool pool;

    protected MathExpression(String scripts, Pool pool) {
        this.script = scripts;
        this.pool = pool;
    }

    protected MathExpression(String scripts, List<String> variables, Pool pool) {
        this(scripts, pool);

        if (variables != null) {
            for (String name : variables) {
//                getPool().allocVar(name);
            }
        }
    }

    public MathExpression compile() {
        if (expression == null) {
            expression = new ExpressionCompiler(pool, script).compute();
        }
        return this;
    }

    @Deprecated()
    public MathExpression forceCompile() {
        expression = new ExpressionCompiler(pool, script).compute();
        return this;
    }
//
//    public MathExpression setVariable(String name, double value) {
//        pool.getVar(name).assign(value);
//        return this;
//    }

//    public MathExpression setVariable(String name, Expression value) {
//        pool.getVar(name).assign(value);
//        return this;
//    }

    public Value eval() {
        compile();
        return expression;
    }
//
//    public double numValue() {
//        compile();
//        return ((Operable) expression).value();
//    }
//
//    @Override
//    public String asString() {
//        eval();
//        return expression.asString();
//    }
//
//    public Expression getExpression() {
//        return expression;
//    }

    public Pool getPool() {
        return pool;
    }
}
