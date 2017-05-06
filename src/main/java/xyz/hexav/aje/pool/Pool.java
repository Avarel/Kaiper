package xyz.hexav.aje.pool;

import com.sun.org.apache.xpath.internal.operations.Variable;
import xyz.hexav.aje.Function;
import xyz.hexav.aje.operators.DefaultOperatorMap;
import xyz.hexav.aje.operators.OperatorMap;

import java.util.HashSet;
import java.util.Set;

public class Pool {
    private final OperatorMap operators;
    //private Set<Variable> variables;
    private Set<Function> functions;

    public Pool() {
        this(DefaultOperatorMap.INSTANCE.copy(), new HashSet<>(), new HashSet<>());
    }

    public Pool(Pool toCopy) { //TODO FIX HERE
        this(toCopy.getOperators(), toCopy.getFunctions(), null);
    }

    public Pool(OperatorMap operators, Set<Function> functions, Set<Variable> variables) {
        this.operators = new OperatorMap(operators);
        this.functions = new HashSet<>(functions);
        //this.variables = new HashSet<>(variables);

        //allocVar("ans");
    }

//    public Variable allocVar(String name) {
//        Variable var = new Variable(name);
//        variables.add(var);
//        return var;
//    }
//
//    public FinalVar allocVal(String name) {
//        FinalVar val = new FinalVar(name);
//        variables.add(val);
//        return val;
//    }
//
//    public void deallocVar(String name) {
//        if (hasVar(name)) {
//            Variable var = getVar(name);
//            var.checkLock(); // Can't deallocate finalized variables.
//
//            variables.remove(getVar(name));
//        }
//    }
//
//    public boolean hasVar(String name) {
//        for (Variable val : variables) {
//            if (val.getName().equals(name)) return true;
//        }
//        return false;
//    }
//
//    public Variable getVar(String name) {
//        for (Variable val : variables) {
//            if (val.getName().equals(name)) {
//                return val;
//            }
//        }
//        return null;
//    }

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

//    public Set<Variable> getVariables() {
//        return variables;
//    }

    public Set<Function> getFunctions() {
        return functions;
    }
}
