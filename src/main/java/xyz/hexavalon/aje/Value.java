package xyz.hexavalon.aje;

import xyz.hexavalon.aje.expressions.Expression;

public class Value extends Variable
{
    public Value(String name)
    {
        this(name, Double.NaN);
    }
    
    public Value(String name, double value)
    {
        super(name, value);
    }
    
    @Override
    public void assign(Expression exp)
    {
        throw new RuntimeException("Attempting to change final value '"+name+"`.");
    }
    
    @Override
    public void assign(double value)
    {
        throw new RuntimeException("Attempting to change final value '"+name+"`.");
    }
    
    @Override
    public void assign(double[] value)
    {
        throw new RuntimeException("Attempting to change final value '"+name+"`.");
    }
}
