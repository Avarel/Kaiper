package xyz.hexavalon.aje;

import xyz.hexavalon.aje.expressions.Expression;

public class Variable
{
    protected final String name;
    protected Expression exp;
    protected double[] result = null;
    
    public Variable(String name)
    {
        this(name, Expression.NOTHING);
    }
    
    public Variable(String name, double result)
    {
        this(name, Expression.NOTHING);
        this.result = new double[] { result };
    }
    
    public Variable(String name, Expression exp)
    {
        this.name = name;
        this.exp = exp;
    }
    
    public double[] eval()
    {
        if (result != null) return result;
        return result = exp.evalList();
    }
    
    public String getName()
    {
        return name;
    }
    
    public void assign(double value)
    {
        result = new double[] { value };
        exp = Expression.NOTHING;
    }
    
    public void assign(double[] value)
    {
        result = value;
        exp = Expression.NOTHING;
    }
    
    public void assign(Expression exp)
    {
        result = null;
        this.exp = exp;
    }
}
