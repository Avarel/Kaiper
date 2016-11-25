package xyz.hexav.aje;

public class ExpressionBuilder extends AbstractBuilder<ExpressionBuilder>
{
    public ExpressionBuilder(String script)
    {
        addLine(script);
    }
    
    public MathExpression build()
    {
        return new MathExpression(getLines(), getPool());
    }
}
