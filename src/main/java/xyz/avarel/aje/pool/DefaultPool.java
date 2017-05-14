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

        pool.put("sqrt", DefaultFunctions.SQUARE_ROOT.getFunction());
        pool.put("cbrt", DefaultFunctions.CUBE_ROOT.getFunction());
        pool.put("exp", DefaultFunctions.EXPONENTIAL.getFunction());
        pool.put("log", DefaultFunctions.LOG10.getFunction());
        pool.put("ln", DefaultFunctions.LOG_NATURAL.getFunction());
        pool.put("round", DefaultFunctions.ROUND.getFunction());
        pool.put("floor", DefaultFunctions.FLOOR.getFunction());
        pool.put("ceil", DefaultFunctions.CEILING.getFunction());

        pool.put("sin", DefaultFunctions.SINE.getFunction());
        pool.put("cos", DefaultFunctions.COSINE.getFunction());
        pool.put("tan", DefaultFunctions.TANGENT.getFunction());
        pool.put("csc", DefaultFunctions.COSECANT.getFunction());
        pool.put("sec", DefaultFunctions.SECANT.getFunction());
        pool.put("cot", DefaultFunctions.COTANGENT.getFunction());

        pool.put("sinh", DefaultFunctions.HYPERBOLIC_SINE.getFunction());
        pool.put("cosh", DefaultFunctions.HYPERBOLIC_COSINE.getFunction());
        pool.put("tanh", DefaultFunctions.HYPERBOLIC_TANGENT.getFunction());

        pool.put("asin", DefaultFunctions.ARCSINE.getFunction());
        pool.put("acos", DefaultFunctions.ARCCOSINE.getFunction());
        pool.put("atan", DefaultFunctions.ARCTANGENT.getFunction());
        pool.put("acsc", DefaultFunctions.ARCCOSECANT.getFunction());
        pool.put("asec", DefaultFunctions.ARCSECANT.getFunction());
        pool.put("acot", DefaultFunctions.ARCCOTANGENT.getFunction());
        pool.put("atan2", DefaultFunctions.ARCTANGENT2.getFunction());

        pool.put("map", DefaultFunctions.MAP.getFunction());
        pool.put("filter", DefaultFunctions.FILTER.getFunction());
        pool.put("fold", DefaultFunctions.FOLD.getFunction());

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
