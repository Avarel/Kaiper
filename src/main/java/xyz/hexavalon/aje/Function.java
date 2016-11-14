package xyz.hexavalon.aje;

import xyz.hexavalon.aje.expressions.Evaluable;
import xyz.hexavalon.aje.expressions.Expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Function implements Evaluable
{
    protected final String script;
    protected final List<Expression> expressions;
    
    protected final String invoker;
    protected final List<String> parameters;
    
    protected final Pool env;
    
    protected final int inputsRequired;
    
    public Function(String script)
    {
        this(script, new String[0]);
    }
    
    public Function(String script, String... parameters)
    {
        this(script, new Pool(), parameters);
    }
    
    public Function(String script, Pool env, String... parameters)
    {
        this(null, script, env, parameters);
    }
    
    Function(String invoker, String script, Pool env, String... parameters)
    {
        this(invoker, script, env, Arrays.asList(parameters));
    }
    
    Function(String invoker, String script, Pool env, List<String> parameters)
    {
        this.invoker = invoker;
        this.script = script;
        this.env = env;
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
            env.variable(parameters.get(i)).assign(args[i]);
        }
    
        if (expressions.isEmpty()) expressions.addAll(env.getCompiler().compileScript(script));
        
        double[] value = new double[0];
        
        for (Expression exp : expressions)
        {
            value = exp.evalList();
            env.variable("ans").assign(value);
        }
        
        return value;
    }
    
    //TODO
    @Override
    public String toString()
    {
        return "func " + (invoker != null ? invoker : Integer.toHexString(hashCode())) + "() = { " + script + " }";
    }
    
    public String getInvoker()
    {
        return invoker;
    }
    
    public List<String> getParameters()
    {
        return parameters;
    }
    
    public int getInputsRequired()
    {
        return inputsRequired;
    }
}