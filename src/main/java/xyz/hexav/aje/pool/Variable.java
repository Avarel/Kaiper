package xyz.hexav.aje.pool;

import xyz.hexav.aje.types.AJENothing;
import xyz.hexav.aje.types.AJEValue;

public class Variable {
    protected final String name;
    protected AJEValue exp;
    protected double result = Double.NaN;

    protected boolean lock = false;

    public Variable(String name) {
        this(name, AJENothing.VALUE);
    }

    public Variable(String name, double result) {
        this(name, AJENothing.VALUE);
        this.result = result;
    }

    public Variable(String name, AJEValue exp) {
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
        exp = AJENothing.VALUE;
    }

    public void assign(AJEValue exp) {
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
