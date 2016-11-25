package xyz.hexav.aje;

import xyz.hexav.aje.pool.Pool;

import java.util.List;

public class MathFunction extends MathExpression implements Function
{
    protected final String name;
    
    private final List<String> parameters;
    
    protected final double[] inputs;
    
    protected MathFunction(String name, List<String> scripts, List<String> parameters, Pool pool)
    {
        super(scripts, parameters, pool);
        
        this.name = name;
        this.parameters = parameters;
        this.inputs = new double[parameters.size()];
    }
    
    @Override
    public Function input(int index, double input)
    {
        inputs[index] = input;
        return this;
    }
    
    @Override
    public Function input(String param, double input)
    {
        return input(getParameters().indexOf(param), input);
    }
    
    @Override
    public Function input(double... inputs)
    {
        for (int i = 0; i < inputs.length; i++)
        {
            input(i, inputs[i]);
        }
        return this;
    }
    
    @Override
    public List<String> getParameters()
    {
        return parameters;
    }
    
    @Override
    public double eval()
    {
        return evalList()[0];
    }

    /** Evaluate and return the result(s) of the function. */
    @Override
    public double[] evalList()
    {
        for (int i = 0; i < inputs.length; i++)
        {
            getPool().getVar(getParameters().get(i)).assign(inputs[i]);
        }
    
        return super.evalList();
    }
    
    @Override
    public String getName()
    {
        return name;
    }
    
    @Override
    public int getParametersCount()
    {
        return parameters.size();
    }
}