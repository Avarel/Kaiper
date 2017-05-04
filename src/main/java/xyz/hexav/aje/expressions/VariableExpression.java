package xyz.hexav.aje.expressions;

import xyz.hexav.aje.pool.Variable;
import xyz.hexav.aje.types.AJEValue;

public class VariableExpression implements AJEValue {
    private final Variable var;

    public VariableExpression(Variable var) {
        this.var = var;
    }

    public double value() {
        return var.eval();
    }

    public Variable getVariable() {
        return var;
    }
}
