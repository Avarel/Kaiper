package xyz.hexav.aje.expressions;

import xyz.hexav.aje.pool.Variable;
import xyz.hexav.aje.types.AJEValue;

public class VariableAssignment extends VariableExpression {
    private final AJEValue exp;

    public VariableAssignment(Variable var) {
        this(var, null);
    }

    public VariableAssignment(Variable var, AJEValue exp) {
        super(var);
        this.exp = exp;
    }

    public AJEValue getExpression() {
        return exp;
    }

    @Override
    public double value() {
        if (exp != null) getVariable().assign(exp.value());
        return getVariable().eval();
    }
}
