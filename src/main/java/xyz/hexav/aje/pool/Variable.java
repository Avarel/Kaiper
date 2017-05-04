package xyz.hexav.aje.pool;

import xyz.hexav.aje.types.Nothing;
import xyz.hexav.aje.types.Expression;

public class Variable {
    protected final String name;
    protected Expression exp;
    protected double result = Double.NaN;

    protected boolean lock = false;

    public Variable(String name) {
        this(name, Nothing.VALUE);
    }

    public Variable(String name, double result) {
        this(name, Nothing.VALUE);
        this.result = result;
    }

    public Variable(String name, Expression exp) {
        this.name = name;
        this.exp = exp;
    }

    public double eval() {
        if (!Double.isNaN(result)) return result;
        return result = exp.value();
    }

    public String getName() {
        return name;
    }

    public void assign(double value) {
        checkLock();

        result = value;
        exp = Nothing.VALUE;
    }

    public void assign(Expression exp) {
        checkLock();

        result = Double.NaN;
        this.exp = exp;
    }

    public void lock() {
        lock = true;
    }

    public boolean isLocked() {
        return lock;
    }

    protected void checkLock() {
        if (lock) {
            throw new RuntimeException("Variable `" + name + "` is final.");
        }
    }
}
