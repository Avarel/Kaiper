package xyz.hexavalon.aje.expressions;

import xyz.hexavalon.aje.pool.Pool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunctionExpression implements Expression
{
    private final Pool pool;
    private final String invoking;
    private final List<Expression> args;
    
    public FunctionExpression(Pool env, String invoking, List<Expression> args)
    {
        this.invoking = invoking;
        this.pool = env;
        this.args = args;
    }
    
    @Override
    public double[] evalList()
    {
        if (!pool.hasFunc(invoking, args.size()))
            throw new RuntimeException("Function '"+invoking+"' isn't defined.");
    
        double[][] lists = new double[args.size()][];
    
        int length = 1;
        int index = 0;
        for (Expression exp : args)
        {
            double[] arr = exp.evalList();
        
            if (exp instanceof SpreadExpression)
            {
                if (arr.length == 1)
                {
                    lists[index] = arr;
                    index++;
                    continue;
                }
            
                // Expand and spread the argument-pool to include
                // each of the list's arguments.
                lists = Arrays.copyOf(lists, lists.length + arr.length - 1);
                
                for (double item : arr)
                {
                    lists[index] = new double[]{item};
                    index++;
                }
            }
            else
            {
                // Result = size of lowest list of which size is not 1.
            
                lists[index] = arr;
            
                if (arr.length != 1 && length == 1) length = arr.length;
                else if (arr.length < length) length = arr.length;
                index++;
            }
        }
    
        double[] results = new double[length];
        
        index = 0;
        for (int i = 0; i < length; i++)
        {
            final List<Double> args0 = new ArrayList<>(lists.length);
            
            for (double[] arg : lists)
            {
                args0.add(arg[i]);
            }
            
            double[] res = pool.getFunc(invoking, args.size()).evalList(args0);

            if (res.length == 1)
            {
                results[i] = res[0];
                index++;
            }
            else
            {
                results = Arrays.copyOf(results, results.length + res.length - 1);
    
                for (double item : res)
                {
                    results[index] = item;
                    index++;
                }
            }
        }
        
        return results;
    }
    
}
