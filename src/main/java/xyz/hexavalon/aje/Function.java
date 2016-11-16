package xyz.hexavalon.aje;

import xyz.hexavalon.aje.expressions.Evaluable;
import xyz.hexavalon.aje.expressions.Expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Function implements Evaluable
{
    private final String script;
    private final List<Expression> expressions;
    
    private final String name;
    private final List<String> parameters;
    
    private final Pool pool;
    
    private final int inputsRequired;

    protected Function(String name, String script, String... parameters)
    {
        this(name, script, new Pool(), Arrays.asList(parameters));
    }

    protected Function(String name, String script, Pool pool, String... parameters)
    {
        this(name, script, pool, Arrays.asList(parameters));
    }
    
    protected Function(String name, String script, Pool pool, List<String> parameters)
    {
        this.name = name;
        this.script = script;
        this.pool = pool;
        this.parameters = parameters;
        this.inputsRequired = parameters.size();
        this.expressions = new ArrayList<>();
    }
    
    public double eval(double... args)
    {
        return evalList(args)[0];
    }

    public double eval(List<Double> args)
    {
        return evalList(args)[0];
    }

    public double[] evalList(List<Double> args)
    {
        double[] _args = new double[args.size()];
        for (int i = 0; i < _args.length; i++) _args[i] = args.get(i);

        return evalList(_args);
    }
    
    /** Evaluate and return the result(s) of the function. */
    public double[] evalList(double... args)
    {
        if (args.length != parameters.size())
            throw new RuntimeException("Insufficient amount of arguments.");
    
        for (int i = 0; i < args.length; i++)
        {
            pool.variable(parameters.get(i)).assign(args[i]);
        }
    
        if (expressions.isEmpty()) expressions.addAll(pool.getCompiler().compileScript(script));
        
        double[] value = new double[0];
        
        for (Expression exp : expressions)
        {
            value = exp.evalList();
            pool.variable("ans").assign(value);
        }
        
        return value;
    }
    
    //TODO
    @Override
    public String toString()
    {
        return "func " + (name != null ? name : Integer.toHexString(hashCode())) + "() = { " + script + " }";
    }
    
    public String getName()
    {
        return name;
    }
    
    public List<String> getParameters()
    {
        return parameters;
    }
    
    public int getInputsRequired()
    {
        return inputsRequired;
    }

    public List<Expression> getExpressions()
    {
        return expressions;
    }
}