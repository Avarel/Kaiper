//package xyz.hexav.aje.pool;
//
//import xyz.hexav.aje.types.Nothing;
//
//public class Variable implements Expression {
//    protected final String name;
//    protected Expression expression;
//
//    protected boolean lock = false;
//
//    public Variable(String name) {
//        this(name, Nothing.VALUE);
//    }
//
//    public Variable(String name, double result) {
//        this(name, Nothing.VALUE);
//    }
//
//    public Variable(String name, Expression expression) {
//        this.name = name;
//        this.expression = expression;
//    }
//
//    public Expression getExpression() {
//        return expression;
//    }
//
//    public Expression eval() {
//        return expression;
//    }
//
//    @Override
//    public String asString() {
//        return expression.asString();
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void assign(double value) {
//        checkLock();
//
//        expression = Value.of(value);
//    }
//
//    public void assign(Expression exp) {
//        checkLock();
//
//        this.expression = exp;
//    }
//
//    public void lock() {
//        lock = true;
//    }
//
//    public boolean isLocked() {
//        return lock;
//    }
//
//    protected void checkLock() {
//        if (lock) {
//            throw new RuntimeException("Variable `" + name + "` is final.");
//        }
//    }
//}
