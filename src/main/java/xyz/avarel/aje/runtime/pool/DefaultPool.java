package xyz.avarel.aje.runtime.pool;

import xyz.avarel.aje.runtime.Slice;
import xyz.avarel.aje.runtime.Truth;
import xyz.avarel.aje.runtime.types.numbers.Complex;
import xyz.avarel.aje.runtime.types.numbers.Decimal;
import xyz.avarel.aje.runtime.types.numbers.Int;

public class DefaultPool extends ObjectPool {
    public static final DefaultPool INSTANCE = new DefaultPool();

    private DefaultPool() {
        put("pi", Decimal.of(Math.PI));
        put("e", Decimal.of(Math.E));

        put("sqrt", DefaultFunctions.SQUARE_ROOT.get());
        put("cbrt", DefaultFunctions.CUBE_ROOT.get());
        put("exp", DefaultFunctions.EXPONENTIAL.get());
        put("log", DefaultFunctions.LOG10.get());
        put("ln", DefaultFunctions.LOG_NATURAL.get());
        put("round", DefaultFunctions.ROUND.get());
        put("floor", DefaultFunctions.FLOOR.get());
        put("ceil", DefaultFunctions.CEILING.get());
        put("sum", DefaultFunctions.SUM.get());
        put("product", DefaultFunctions.PRODUCT.get());

        put("sin", DefaultFunctions.SINE.get());
        put("cos", DefaultFunctions.COSINE.get());
        put("tan", DefaultFunctions.TANGENT.get());
        put("csc", DefaultFunctions.COSECANT.get());
        put("sec", DefaultFunctions.SECANT.get());
        put("cot", DefaultFunctions.COTANGENT.get());

        put("sinh", DefaultFunctions.HYPERBOLIC_SINE.get());
        put("cosh", DefaultFunctions.HYPERBOLIC_COSINE.get());
        put("tanh", DefaultFunctions.HYPERBOLIC_TANGENT.get());
        put("csch", DefaultFunctions.HYPERBOLIC_COSECANT.get());
        put("sech", DefaultFunctions.HYPERBOLIC_SECANT.get());
        put("coth", DefaultFunctions.HYPERBOLIC_COTANGENT.get());

        put("asin", DefaultFunctions.ARCSINE.get());
        put("acos", DefaultFunctions.ARCCOSINE.get());
        put("atan", DefaultFunctions.ARCTANGENT.get());
        put("acsc", DefaultFunctions.ARCCOSECANT.get());
        put("asec", DefaultFunctions.ARCSECANT.get());
        put("acot", DefaultFunctions.ARCCOTANGENT.get());
        put("atan2", DefaultFunctions.ARCTANGENT2.get());

        put("map", DefaultFunctions.MAP.get());
        put("filter", DefaultFunctions.FILTER.get());
        put("fold", DefaultFunctions.FOLD.get());

        // Types
        put("Int", Int.TYPE);
        put("Decimal", Decimal.TYPE);
        put("Complex", Complex.TYPE);
        put("Boolean", Truth.TYPE);
        put("Slice", Slice.TYPE);
    }
}
