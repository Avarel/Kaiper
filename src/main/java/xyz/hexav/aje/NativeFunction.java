package xyz.hexav.aje;

import xyz.hexav.aje.expressions.Evaluable;

import java.util.ArrayList;
import java.util.List;

public class NativeFunction implements Function
{
    protected final String name;
    
    private Evaluable evaluator;
    protected final double[] inputs;
    
    private List<String> parameters;
    
    public NativeFunction(String name, int argumentCount)
    {
        this(name, argumentCount, null);
    }
    
    // 0 args size = varargs
    public NativeFunction(String name, int argumentCount, Evaluable evaluator)
    {
        this.name = name;
        
        this.parameters = new ArrayList<>(argumentCount);
        this.inputs = new double[argumentCount];
        
        this.evaluator = evaluator;
        
        for(int i = 0; i < argumentCount; i++)
        {
            parameters.add(i, "var".concat(Integer.toString(i)));
        }
    }
    
    public double[] evalList()
    {
        return new double[] { evaluator.eval(inputs) };
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
    public String getName()
    {
        return name;
    }
    
    @Override
    public int getArgumentCount()
    {
        return parameters.size();
    }
}
