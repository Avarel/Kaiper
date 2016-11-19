package xyz.hexavalon.aje.defaults;

import xyz.hexavalon.aje.Function;
import xyz.hexavalon.aje.expressions.Evaluable;

import java.util.ArrayList;

public class NativeFunction extends Function
{
    private Evaluable evaluator;
    private final int inputsRequired;
    
    public NativeFunction(String name, int inputsRequired)
    {
        this(name, inputsRequired, null);
    }
    
    // 0 args size = varargs
    public NativeFunction(String name, int inputsRequired, Evaluable evaluator)
    {
        super(name, null, null, new ArrayList<>(inputsRequired));
        this.evaluator = evaluator;
        this.inputsRequired = inputsRequired;
    }
    
    @Override
    public double[] evalList(double... args)
    {
        if (inputsRequired != 0 && args.length != inputsRequired)
            throw new RuntimeException("Insufficient amount of arguments.");
        
        return new double[] { evaluator.eval(args) };
    }
    
    @Override
    public int getInputsRequired()
    {
        return inputsRequired;
    }
}
