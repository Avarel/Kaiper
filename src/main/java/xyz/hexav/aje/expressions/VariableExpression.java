package xyz.hexav.aje.expressions;

import xyz.hexav.aje.pool.Variable;
import xyz.hexav.aje.types.Expression;

public class VariableExpression implements Expression {
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
