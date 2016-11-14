package xyz.hexavalon.aje;

import xyz.hexavalon.aje.expressions.ExpressionScriptCompiler;
import xyz.hexavalon.aje.operators.Operators;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Pool
{
    private final static Set<Function> nativeFunctions = new HashSet<>();

    static
    {
        nativeFunctions.add(new NativeFunction("abs", 1, args -> Math.abs(args[0])));
        nativeFunctions.add(new NativeFunction("sqrt", 1, args -> Math.sqrt(args[0])));
        nativeFunctions.add(new NativeFunction("cbrt", 1, args -> Math.cbrt(args[0])));
        
        nativeFunctions.add(new NativeFunction("floor", 1, args -> Math.floor(args[0])));
        nativeFunctions.add(new NativeFunction("ceil", 1, args -> Math.ceil(args[0])));
        nativeFunctions.add(new NativeFunction("round", 1, args -> Math.round(args[0])));
        nativeFunctions.add(new NativeFunction("signum", 1, args -> Math.signum(args[0])));
        
        nativeFunctions.add(new NativeFunction("max", 2, args -> Math.max(args[0], args[1])));
        nativeFunctions.add(new NativeFunction("min", 2, args -> Math.min(args[0], args[1])));
    
        nativeFunctions.add(new NativeFunction("ln", 1, args -> Math.log(args[0])));
        nativeFunctions.add(new NativeFunction("log", 1, args -> Math.log10(args[0])));
        nativeFunctions.add(new NativeFunction("exp", 1, args -> Math.exp(args[0])));
        
        nativeFunctions.add(new NativeFunction("sin", 1, args -> Math.sin(args[0])));
        nativeFunctions.add(new NativeFunction("cos", 1, args -> Math.cos(args[0])));
        nativeFunctions.add(new NativeFunction("tan", 1, args -> Math.tan(args[0])));
        nativeFunctions.add(new NativeFunction("asin", 1, args -> Math.asin(args[0])));
        nativeFunctions.add(new NativeFunction("acos", 1, args -> Math.acos(args[0])));
        nativeFunctions.add(new NativeFunction("atan", 1, args -> Math.atan(args[0])));
        nativeFunctions.add(new NativeFunction("atan2", 2, args -> Math.atan2(args[0], args[1])));
    
        nativeFunctions.add(new NativeFunction("sinh", 1, args -> Math.sinh(args[0])));
        nativeFunctions.add(new NativeFunction("cosh", 1, args -> Math.cosh(args[0])));
        nativeFunctions.add(new NativeFunction("tanh", 1, args -> Math.tanh(args[0])));
        
        nativeFunctions.add(new NativeFunction("size", 0) {
            @Override
            public double[] evalList(double... args)
            {
                return new double[] { args.length };
            }
        });
        
        nativeFunctions.add(new NativeFunction("sum", 0, args -> {
            double sum = 0;
            for (double i : args) sum += i;
            return sum;
        }));
        nativeFunctions.add(new NativeFunction("avg", 0, args -> {
            double sum = 0;
            for (double i : args) sum += i;
            return sum / args.length;
        }));
        nativeFunctions.add(new NativeFunction("prod", 0, args -> {
            double sum = args[0];
            for (int i = 1; i < args.length; i++) sum *= args[i];
            return sum;
        }));
    
        nativeFunctions.add(new NativeFunction("quadrt", 3) {
            @Override
            public double[] evalList(double... args)
            {
                double[] ans = new double[2];
                ans[0] = (-args[1] + Math.sqrt(Math.pow(args[1], 2) - (4 * args[0] * args[2]))) / (2 * args[0]);
                ans[1] = (-args[1] - Math.sqrt(Math.pow(args[1], 2) - (4 * args[0] * args[2]))) / (2 * args[0]);
                return ans;
            }
        });
    }
    
    private Set<Variable> variables = new HashSet<>();
    
    private Set<Function> functions = new HashSet<>(nativeFunctions);
    
    private final Operators operators = Operators.getDefaultOperators();
    
    private final ExpressionScriptCompiler compiler = new ExpressionScriptCompiler(this);
    
    public Pool()
    {
        variable("ans");
        variable("pi").assign(Math.PI);
        variable("π").assign(Math.PI);
        variable("e").assign(Math.E);
        variable("φ").assign(1.61803398875);
    }
    
    public Variable variable(String name)
    {
        Variable var;
        
        if (hasVariable(name))
        {
            var = getVariable(name);
        }
        else getVariables().add(var = new Variable(name));
        
        return var;
    }
    
    public boolean hasVariable(String name)
    {
        for (Variable val : variables)
        {
            if (val.getName().equals(name)) return true;
        }
        return false;
    }
    
    public Variable getVariable(String name)
    {
        for (Variable val : variables)
        {
            if (val.getName().equals(name)) return val;
        }
        return null;
    }
    
    public void allocateFunction(Function function)
    {
        functions.add(function);
    }
    
    public void allocateFunction(String name, String script, List<String> parameters)
    {
        functions.add(new Function(name, script, this.copy(), parameters));
    }
    
    public boolean hasFunction(String name)
    {
        for (Function func : functions)
        {
            if (func.getInvoker().equals(name))
            {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean hasFunction(String name, int argsSize)
    {
        for (Function func : functions)
        {
            if (func.getInvoker().equals(name))
            {
                if (func.getInputsRequired() == argsSize)
                {
                    return true;
                }
            }
        }
    
        // Varargs lowest priority.
        for (Function func : functions)
        {
            if (func.getInvoker().equals(name))
            {
                if (func.getInputsRequired() == 0)
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Function getFunction(String name, int argsSize)
    {
        for (Function func : functions)
        {
            if (func.getInvoker().equals(name))
            {
                if (func.getInputsRequired() == argsSize)
                {
                    return func;
                }
            }
        }
        
        // Varargs lowest priority.
        for (Function func : functions)
        {
            if (func.getInvoker().equals(name))
            {
                if (func.getInputsRequired() == 0)
                {
                    return func;
                }
            }
        }
        return null;
    }
    
    public Operators getOperators()
    {
        return operators;
    }
    
    public ExpressionScriptCompiler getCompiler()
    {
        return compiler;
    }
    
    public Pool copy()
    {
        Pool clone = new Pool();
        clone.variables = new HashSet<>(variables);
        clone.functions = new LinkedHashSet<>(functions);
        return clone;
    }
    
    public Set<Variable> getVariables()
    {
        return variables;
    }
    
    public Set<Function> getFunctions()
    {
        return functions;
    }
}
