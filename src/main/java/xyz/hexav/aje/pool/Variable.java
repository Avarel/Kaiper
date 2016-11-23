package xyz.hexav.aje.pool;

import xyz.hexav.aje.expressions.Expression;

public class Variable
{
    protected final String name;
    protected Expression exp;
    protected double[] result = null;
    
    protected boolean lock = false;
    
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
        checkLock();
        
        result = new double[] { value };
        exp = Expression.NOTHING;
    }
    
    public void assign(double[] value)
    {
        checkLock();
        
        result = value;
        exp = Expression.NOTHING;
    }
    
    public void assign(Expression exp)
    {
        checkLock();
        
        result = null;
        this.exp = exp;
    }
    
    public void lock()
    {
        lock = true;
    }
    
    public boolean isLocked()
    {
        return lock;
    }
    
    protected void checkLock()
    {
        if (lock)
        {
            throw new RuntimeException("Variable `" + name + "` is final.");
        }
    }
}
