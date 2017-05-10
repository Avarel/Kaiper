package xyz.avarel.aje.pool;

import xyz.avarel.aje.defaults.DefaultFunction;
import xyz.avarel.aje.types.numbers.Complex;
import xyz.avarel.aje.types.numbers.Decimal;
import xyz.avarel.aje.types.numbers.Int;
import xyz.avarel.aje.types.others.Slice;
import xyz.avarel.aje.types.others.Truth;

public class DefaultPool extends Pool {
    public static DefaultPool INSTANCE = new DefaultPool();
    
    private DefaultPool() {
        put("pi", Decimal.of(Math.PI));
        put("e", Decimal.of(Math.E));

        put("sin", DefaultFunction.SIN);
        put("cos", DefaultFunction.COS);
        put("tan", DefaultFunction.TAN);
        put("asin", DefaultFunction.ASIN);
        put("acos", DefaultFunction.ACOS);
        put("atan", DefaultFunction.ATAN);
        put("atan2", DefaultFunction.ATAN2);
        put("map", DefaultFunction.MAP);

        put("max", DefaultFunction.MAX);
        put("min", DefaultFunction.MIN);

        // Types
        put("Int", Int.TYPE);
        put("Decimal", Decimal.TYPE);
        put("Complex", Complex.TYPE);
        put("Boolean", Truth.TYPE);
        put("Slice", Slice.TYPE);

    }
}
