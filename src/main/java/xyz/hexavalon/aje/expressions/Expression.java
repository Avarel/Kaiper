package xyz.hexavalon.aje.expressions;

import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
public interface Expression
{
    double[] EMPTY_DOUBLE_ARRAY = new double[0];
    Expression NOTHING = () -> EMPTY_DOUBLE_ARRAY;
    
    double[] evalList();
    
    default double eval()
    {
        double[] list = evalList();
        return list.length > 0 ? list[0] : Double.NaN;
    }
    
    static Expression ofValue(double value)
    {
        return ofValues(new double[] { value });
    }
    
    static Expression ofValues(double[] values)
    {
        return () -> values;
    }
    
    default SpreadExpression spread()
    {
        return new SpreadExpression(this);
    }
    
    static Expression ofRange(Expression a, Expression b, Expression c)
    {
        return () ->
        {
            final int
                    init = (int) a.eval(),
                    end = (int) b.eval(),
                    before = c != null ? (int) c.eval() : init - 1,
                    delta = Math.abs(init - before);
    
            int length = Math.abs(init - end) / delta + 1;
    
            double[] i_range = new double[length];
            
            if (init < end)
            {
                for (int i = 0; i < length; i++)
                {
                    i_range[i] = (double) (i * delta) + init;
                }
            }
            else
            {
                for (int i = 0; i < length; i++)
                {
                    i_range[i] = (double) -(i * delta) + init;
                }
            }
    
            return i_range;
        };
    }
    
    static Expression ofList(List<Expression> items)
    {
        return () ->
        {
            List<Double> list = new ArrayList<>();
    
            for (Expression exp : items)
            {
                for (double d : exp.evalList())
                {
                    list.add(d);
                }
            }
    
            double[] _list = new double[list.size()];
            for (int i = 0; i < _list.length; i++) _list[i] = list.get(i);
    
            return _list;
        };
    }
    
    // A heads up if spreading the expression.
    class SpreadExpression implements Expression
    {
        private final Expression exp;
    
        public SpreadExpression(Expression exp)
        {
            this.exp = exp;
        }
        
        @Override
        public double[] evalList()
        {
            return exp.evalList();
        }
    }
}
