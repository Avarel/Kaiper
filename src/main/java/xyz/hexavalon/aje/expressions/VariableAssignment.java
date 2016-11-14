package xyz.hexavalon.aje.expressions;

import xyz.hexavalon.aje.Variable;

public class VariableAssignment extends VariableExpression
{
    private final Expression exp;
    
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
        getVariable().assign(exp.evalList());
        return getVariable().eval();
    }
}
