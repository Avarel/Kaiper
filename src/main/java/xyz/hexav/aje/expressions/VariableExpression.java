package xyz.hexav.aje.expressions;

import xyz.hexav.aje.pool.Variable;

public class VariableExpression implements Expression
{
    private final Variable var;
    
    public VariableExpression(Variable var)
    {
        this.var = var;
    }
    
    @Override
    public double[] evalList()
    {
        return var.eval();
    }
    
    public Variable getVariable()
    {
        return var;
    }
}
