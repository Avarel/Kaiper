package xyz.hexav.aje.pool;

import xyz.hexav.aje.Function;
import xyz.hexav.aje.defaults.DefaultFunctions;
import xyz.hexav.aje.operators.OperatorMap;

import java.util.HashSet;
import java.util.Set;

public class Pool {
    private final static Pool nativePool = new Pool();

    static {
        nativePool.allocVal("pi").assign(Math.PI);
        nativePool.allocVal("e").assign(Math.E);
        nativePool.allocVal("golden_ratio").assign(1.61803398875);

        nativePool.allocFunc(DefaultFunctions.ABSOLUTE_VALUE.get());
        nativePool.allocFunc(DefaultFunctions.SQUARE_ROOT.get());
        nativePool.allocFunc(DefaultFunctions.CUBE_ROOT.get());

        nativePool.allocFunc(DefaultFunctions.FLOOR.get());
        nativePool.allocFunc(DefaultFunctions.CEILING.get());
        nativePool.allocFunc(DefaultFunctions.ROUND.get());
        nativePool.allocFunc(DefaultFunctions.SIGN.get());

        nativePool.allocFunc(DefaultFunctions.MAX.get());
        nativePool.allocFunc(DefaultFunctions.MIN.get());

        nativePool.allocFunc(DefaultFunctions.LOG_NATURAL.get());
        nativePool.allocFunc(DefaultFunctions.LOG_10.get());
        nativePool.allocFunc(DefaultFunctions.EXPONENTIAL.get());

        nativePool.allocFunc(DefaultFunctions.SINE.get());
        nativePool.allocFunc(DefaultFunctions.COSINE.get());
        nativePool.allocFunc(DefaultFunctions.TANGENT.get());
        nativePool.allocFunc(DefaultFunctions.ARCSINE.get());
        nativePool.allocFunc(DefaultFunctions.ARCCOSINE.get());
        nativePool.allocFunc(DefaultFunctions.ARCTANGENT.get());
        nativePool.allocFunc(DefaultFunctions.ARCTANGENT2.get());

        nativePool.allocFunc(DefaultFunctions.HYPERBOLIC_SINE.get());
        nativePool.allocFunc(DefaultFunctions.HYPERBOLIC_COSINE.get());
        nativePool.allocFunc(DefaultFunctions.HYPERBOLIC_TANGENT.get());

//        nativePool.allocFunc(DefaultFunctions.LIST_SIZE.get());
//        nativePool.allocFunc(DefaultFunctions.SUMMATION.get());
//        nativePool.allocFunc(DefaultFunctions.AVERAGE.get());
//        nativePool.allocFunc(DefaultFunctions.PRODUCT.get());

        nativePool.allocFunc(DefaultFunctions.QUADRATIC_ROOT.get());
    }

    private final OperatorMap operators;
    private Set<Variable> variables;
    private Set<Function> functions;

    public Pool() {
        this(OperatorMap.getDefaultOperators(), new HashSet<>(), new HashSet<>());
    }

    public Pool(Pool toCopy) {
        this(toCopy.getOperators(), toCopy.getFunctions(), toCopy.getVariables());
    }

    public Pool(OperatorMap operators, Set<Function> functions, Set<Variable> variables) {
        this.operators = new OperatorMap(operators);
        this.functions = new HashSet<>(functions);
        this.variables = new HashSet<>(variables);

        allocVar("ans");
    }

    public static Pool getDefaultPool() {
        return nativePool.copy();
    }

    public Variable allocVar(String name) {
        Variable var = new Variable(name);
        variables.add(var);
        return var;
    }

    public Value allocVal(String name) {
        Value val = new Value(name);
        variables.add(val);
        return val;
    }

    public void deallocVar(String name) {
        if (hasVar(name)) {
            Variable var = getVar(name);
            var.checkLock(); // Can't deallocate finalized variables.

            variables.remove(getVar(name));
        }
    }

    public boolean hasVar(String name) {
        for (Variable val : variables) {
            if (val.getName().equals(name)) return true;
        }
        return false;
    }

    public Variable getVar(String name) {
        for (Variable val : variables) {
            if (val.getName().equals(name)) return val;
        }
        return null;
    }

    public void allocFunc(Function function) {
        if (function.getName() == null || function.getName().isEmpty()) {
            throw new RuntimeException("Declared functions' name can't be empty.");
        }
        functions.add(function);
    }

    public boolean hasFunc(String name) {
        for (Function func : functions) {
            if (func.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasFunc(String name, int argsSize) {
        for (Function func : functions) {
            if (func.getName().equals(name)) {
                if (func.getParametersCount() == argsSize) {
                    return true;
                }
            }
        }

        // Varargs lowest priority.
        for (Function func : functions) {
            if (func.getName().equals(name)) {
                if (func.getParametersCount() == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public Function getFunc(String name, int argsSize) {
        for (Function func : functions) {
            if (func.getName().equals(name)) {
                if (func.getParametersCount() == argsSize) {
                    return func;
                }
            }
        }

        // Varargs lowest priority.
        for (Function func : functions) {
            if (func.getName().equals(name)) {
                if (func.getParametersCount() == 0) {
                    return func;
                }
            }
        }
        return null;
    }

    public OperatorMap getOperators() {
        return operators;
    }

    public Pool copy() {
        return new Pool(this);
    }

    public Set<Variable> getVariables() {
        return variables;
    }

    public Set<Function> getFunctions() {
        return functions;
    }
}
