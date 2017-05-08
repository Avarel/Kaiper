package xyz.avarel.aje.pool;

import xyz.avarel.aje.defaults.DefaultFunction;

public class DefaultPool extends Pool {
    public static DefaultPool INSTANCE = new DefaultPool();
    
    private DefaultPool() {
        put("sin", DefaultFunction.SIN);
        put("cos", DefaultFunction.COS);
        put("tan", DefaultFunction.TAN);
        put("asin", DefaultFunction.ASIN);
        put("acos", DefaultFunction.ACOS);
        put("atan", DefaultFunction.ATAN);
        put("atan2", DefaultFunction.ATAN2);
    }
}
