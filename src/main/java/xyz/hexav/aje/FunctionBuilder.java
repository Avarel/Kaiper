package xyz.hexav.aje;

import xyz.hexav.aje.operators.Operator;
import xyz.hexav.aje.operators.Precedence;
import xyz.hexav.aje.pool.Pool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunctionBuilder extends ExpressionBuilder
{
    private String name;
    private List<String> parameters;
    
    public FunctionBuilder(String name)
    {
        super();
        
        this.name = name;
        this.parameters = new ArrayList<>();
    }
    
    public FunctionBuilder(String name, String script)
    {
        this(name);
        addLine(script);
    }

    public FunctionBuilder addParameter(String... parameters)
    {
        return addParameter(Arrays.asList(parameters));
    }

    public FunctionBuilder addParameter(List<String> parameters)
    {
        this.parameters.addAll(parameters);
        return this;
    }
    
    public FunctionBuilder addLine(String script)
    {
        super.addLine(script);
        return this;
    }
    
    public FunctionBuilder addLine(int i, String script)
    {
        super.addLine(i, script);
        return this;
    }
    
    public FunctionBuilder removeLine(int i)
    {
        super.removeLine(i);
        return this;
    }
    
    
    public FunctionBuilder setPool(Pool pool)
    {
        super.setPool(pool);
        return this;
    }
    
    public FunctionBuilder addVariable(String... variables)
    {
        return addVariable(Arrays.asList(variables));
    }
    
    public FunctionBuilder addVariable(List<String> variables)
    {
        super.addVariable(variables);
        return this;
    }
    
    
    public FunctionBuilder addFunction(Function function)
    {
        super.addFunction(function);
        return this;
    }
    
    public FunctionBuilder addOperator(Operator operator)
    {
        return addOperator(Precedence.INFIX, operator);
    }
    
    public FunctionBuilder addOperator(int precedence, Operator operator)
    {
        super.addOperator(precedence, operator);
        return this;
    }
    
    public MathFunction build()
    {
        return new MathFunction(name, getScripts(), parameters, getPool());
    }
}
