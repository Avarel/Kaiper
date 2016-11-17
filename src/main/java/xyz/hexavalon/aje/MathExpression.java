package xyz.hexavalon.aje;

import xyz.hexavalon.aje.expressions.Expression;

import java.util.ArrayList;
import java.util.List;

public class MathExpression implements Expression
{
    private final String script;
    private final List<Expression> expressions;

    private final Pool pool;

    public MathExpression(String script)
    {
        this.script = script;
        this.pool = new Pool();
        this.expressions = new ArrayList<>();
    }

    public MathExpression setVariable(String name, double value)
    {
        pool.allocVar(name).assign(value);
        return this;
    }

    public double eval()
    {
        return evalList()[0];
    }

    /** Evaluate and return the result(s) of the function. */
    public double[] evalList()
    {
        if (expressions.isEmpty())
        {
            expressions.addAll(pool.getCompiler().compileScript(script));
        }

        double[] value = new double[0];

        for (Expression exp : expressions)
        {
            value = exp.evalList();
            pool.getVar("ans").assign(value);
        }

        return value;
    }
}
