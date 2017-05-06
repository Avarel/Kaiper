//package xyz.hexav.aje.pool;
//
//import com.sun.org.apache.xpath.internal.operations.Variable;
//
//public class FinalVar extends Variable {
//    public FinalVar(String name) {
//        this(name, Double.NaN);
//    }
//
//    public FinalVar(String name, double value) {
//        super(name, value);
//    }
//
//    @Override
//    public void assign(Expression exp) {
//        checkLock();
//        super.assign(exp);
//        lock();
//    }
//
//    @Override
//    public void assign(double value) {
//        checkLock();
//        super.assign(value);
//        lock();
//    }
//}
