package xyz.hexavalon.aje;

import xyz.hexavalon.aje.defaults.NativeFunction;
import xyz.hexavalon.aje.expressions.Evaluable;
import xyz.hexavalon.aje.pool.Pool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunctionBuilder
{
    private String script;
    private Evaluable evaluator;

    private String name;
    private List<String> parameters;

    private Pool pool;

    public FunctionBuilder()
    {
        parameters = new ArrayList<>();
    }

    public FunctionBuilder setName(String name)
    {
        this.name = name;
        return this;
    }

    public FunctionBuilder setScript(String script)
    {
        this.script = script;
        this.evaluator = null;
        return this;
    }
    
    public FunctionBuilder setEvaluable(Evaluable evaluator)
    {
        this.evaluator = evaluator;
        this.script = null;
        return this;
    }

    public FunctionBuilder setPool(Pool pool)
    {
        this.pool = pool;
        return this;
    }

    public Pool getPool()
    {
        return pool = pool != null ? pool : new Pool();
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

    public Function build()
    {
        return evaluator == null
                ? new Function(name, script, getPool(), parameters)
                : new NativeFunction(name, parameters.size(), evaluator);
    }
}
