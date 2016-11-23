package xyz.hexav.aje.expressions;

import xyz.hexav.aje.pool.Variable;

public class VariableAssignment extends VariableExpression
{
    private final Expression exp;
    
    public VariableAssignment(Variable var)
    {
        this(var, null);
    }
    
    public VariableAssignment(Variable var, Expression exp)
    {
        super(var);
        this.exp = exp;
    }
    
    public Expression getExpression()
    {
        return exp;
    }
    
    @Override
    public double[] evalList()
    {
        if (exp != null) getVariable().assign(exp.evalList());
        return getVariable().eval();
    }
}
