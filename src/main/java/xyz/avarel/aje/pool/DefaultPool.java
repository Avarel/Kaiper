package xyz.avarel.aje.pool;

import xyz.avarel.aje.types.Any;
import xyz.avarel.aje.types.Slice;
import xyz.avarel.aje.types.Truth;
import xyz.avarel.aje.types.numbers.Complex;
import xyz.avarel.aje.types.numbers.Decimal;
import xyz.avarel.aje.types.numbers.Int;

import java.util.HashMap;
import java.util.Map;

public class DefaultPool {
    private static Map<String, Any> pool = new HashMap<>();
    
    static {
        pool.put("pi", Decimal.of(Math.PI));
        pool.put("e", Decimal.of(Math.E));

        pool.put("sqrt", DefaultFunctions.SQUARE_ROOT.get());
        pool.put("cbrt", DefaultFunctions.CUBE_ROOT.get());
        pool.put("exp", DefaultFunctions.EXPONENTIAL.get());
        pool.put("log", DefaultFunctions.LOG10.get());
        pool.put("ln", DefaultFunctions.LOG_NATURAL.get());
        pool.put("round", DefaultFunctions.ROUND.get());
        pool.put("floor", DefaultFunctions.FLOOR.get());
        pool.put("ceil", DefaultFunctions.CEILING.get());

        pool.put("sin", DefaultFunctions.SINE.get());
        pool.put("cos", DefaultFunctions.COSINE.get());
        pool.put("tan", DefaultFunctions.TANGENT.get());
        pool.put("csc", DefaultFunctions.COSECANT.get());
        pool.put("sec", DefaultFunctions.SECANT.get());
        pool.put("cot", DefaultFunctions.COTANGENT.get());

        pool.put("sinh", DefaultFunctions.HYPERBOLIC_SINE.get());
        pool.put("cosh", DefaultFunctions.HYPERBOLIC_COSINE.get());
        pool.put("tanh", DefaultFunctions.HYPERBOLIC_TANGENT.get());
        pool.put("csch", DefaultFunctions.HYPERBOLIC_COSECANT.get());
        pool.put("sech", DefaultFunctions.HYPERBOLIC_SECANT.get());
        pool.put("coth", DefaultFunctions.HYPERBOLIC_COTANGENT.get());

        pool.put("asin", DefaultFunctions.ARCSINE.get());
        pool.put("acos", DefaultFunctions.ARCCOSINE.get());
        pool.put("atan", DefaultFunctions.ARCTANGENT.get());
        pool.put("acsc", DefaultFunctions.ARCCOSECANT.get());
        pool.put("asec", DefaultFunctions.ARCSECANT.get());
        pool.put("acot", DefaultFunctions.ARCCOTANGENT.get());
        pool.put("atan2", DefaultFunctions.ARCTANGENT2.get());

        pool.put("map", DefaultFunctions.MAP.get());
        pool.put("filter", DefaultFunctions.FILTER.get());
        pool.put("fold", DefaultFunctions.FOLD.get());

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
