package xyz.hexav.aje.pool;

import xyz.hexav.aje.types.Expression;

public class Value extends Variable {
    public Value(String name) {
        this(name, Double.NaN);
    }

    public Value(String name, double value) {
        super(name, value);
    }

    @Override
    public void assign(Expression exp) {
        checkLock();
        super.assign(exp);
        lock();
    }

    @Override
    public void assign(double value) {
        checkLock();
        super.assign(value);
        lock();
    }
}
