package xyz.hexav.aje;

import xyz.hexav.aje.operators.Operator;
import xyz.hexav.aje.operators.Precedence;
import xyz.hexav.aje.pool.Pool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExpressionBuilder
{
    private List<String> scripts;

    private Pool pool;

    public ExpressionBuilder()
    {
        scripts = new ArrayList<>();
    }
    
    public ExpressionBuilder(String script)
    {
        this();
        addLine(script);
    }
    
    public ExpressionBuilder addLine(String script)
    {
        scripts.add(script);
        return this;
    }
    
    public ExpressionBuilder addLine(int i, String script)
    {
        scripts.add(i, script);
        return this;
    }
    
    public ExpressionBuilder removeLine(int i)
    {
        scripts.remove(i);
        return this;
    }

    
    public ExpressionBuilder setPool(Pool pool)
    {
        this.pool = pool;
        return this;
    }
    
    public ExpressionBuilder addVariable(String... variables)
    {
        return addVariable(Arrays.asList(variables));
    }
    
    public ExpressionBuilder addVariable(List<String> variables)
    {
        for (String var : variables)
        {
            getPool().allocVar(var);
        }
        return this;
    }
    
    
    public ExpressionBuilder addFunction(Function function)
    {
        getPool().allocFunc(function);
        return this;
    }
    
    public ExpressionBuilder addOperator(Operator operator)
    {
        return addOperator(Precedence.INFIX, operator);
    }
    
    public ExpressionBuilder addOperator(int precedence, Operator operator)
    {
        getPool().getOperators().register(precedence, operator);
        return this;
    }
    
    public Pool getPool()
    {
        return pool != null ? pool : (pool = Pool.getDefaultPool());
    }
    
    public List<String> getScripts()
    {
        return scripts;
    }
    
    public MathExpression build()
    {
        return new MathExpression(scripts, getPool());
    }
}
