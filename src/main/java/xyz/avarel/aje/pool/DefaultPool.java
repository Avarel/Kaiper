package xyz.avarel.aje.pool;

import xyz.avarel.aje.defaults.Functions;

public class DefaultPool extends Pool {
    public static DefaultPool INSTANCE = new DefaultPool();
    
    private DefaultPool() {
        put("sin", Functions.SIN);
        put("cos", Functions.COS);
        put("tan", Functions.TAN);
        put("asin", Functions.ASIN);
        put("acos", Functions.ACOS);
        put("atan", Functions.ATAN);
        put("atan2", Functions.ATAN2);
    }
}
