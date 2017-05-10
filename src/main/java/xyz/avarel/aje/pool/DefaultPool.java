package xyz.avarel.aje.pool;

import xyz.avarel.aje.defaults.DefaultFunction;
import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.numbers.Complex;
import xyz.avarel.aje.types.numbers.Decimal;
import xyz.avarel.aje.types.numbers.Int;
import xyz.avarel.aje.types.others.Slice;
import xyz.avarel.aje.types.others.Truth;

import java.util.HashMap;
import java.util.Map;

public class DefaultPool {
    private static Map<String, Any> pool = new HashMap<>();
    
    static {
        pool.put("pi", Decimal.of(Math.PI));
        pool.put("e", Decimal.of(Math.E));

        pool.put("sin", DefaultFunction.SIN);
        pool.put("cos", DefaultFunction.COS);
        pool.put("tan", DefaultFunction.TAN);
        pool.put("asin", DefaultFunction.ASIN);
        pool.put("acos", DefaultFunction.ACOS);
        pool.put("atan", DefaultFunction.ATAN);
        pool.put("atan2", DefaultFunction.ATAN2);
        pool.put("map", DefaultFunction.MAP);

        pool.put("max", DefaultFunction.MAX);
        pool.put("min", DefaultFunction.MIN);

        // Types
        pool.put("Int", Int.TYPE);
        pool.put("Decimal", Decimal.TYPE);
        pool.put("Complex", Complex.TYPE);
        pool.put("Boolean", Truth.TYPE);
        pool.put("Slice", Slice.TYPE);
    }

    public static Map<String, Any> copy() {
        return new HashMap<>(pool);
    }
}
