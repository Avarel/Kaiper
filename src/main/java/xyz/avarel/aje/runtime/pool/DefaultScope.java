package xyz.avarel.aje.runtime.pool;

import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.numbers.Decimal;

public class DefaultScope extends Scope {
    public static final DefaultScope INSTANCE = new DefaultScope();

    private DefaultScope() {
        assign("pi", Decimal.of(Math.PI));
        assign("e", Decimal.of(Math.E));

        assign("compose", DefaultFunctions.COMPOSE.get());

        assign("sqrt", DefaultFunctions.SQUARE_ROOT.get());
        assign("cbrt", DefaultFunctions.CUBE_ROOT.get());
        assign("exp", DefaultFunctions.EXPONENTIAL.get());
        assign("log", DefaultFunctions.LOG10.get());
        assign("ln", DefaultFunctions.LOG_NATURAL.get());
        assign("round", DefaultFunctions.ROUND.get());
        assign("floor", DefaultFunctions.FLOOR.get());
        assign("ceil", DefaultFunctions.CEILING.get());
        assign("sum", DefaultFunctions.SUM.get());
        assign("product", DefaultFunctions.PRODUCT.get());

        assign("sin", DefaultFunctions.SINE.get());
        assign("cos", DefaultFunctions.COSINE.get());
        assign("tan", DefaultFunctions.TANGENT.get());
        assign("csc", DefaultFunctions.COSECANT.get());
        assign("sec", DefaultFunctions.SECANT.get());
        assign("cot", DefaultFunctions.COTANGENT.get());

        assign("sinh", DefaultFunctions.HYPERBOLIC_SINE.get());
        assign("cosh", DefaultFunctions.HYPERBOLIC_COSINE.get());
        assign("tanh", DefaultFunctions.HYPERBOLIC_TANGENT.get());
        assign("csch", DefaultFunctions.HYPERBOLIC_COSECANT.get());
        assign("sech", DefaultFunctions.HYPERBOLIC_SECANT.get());
        assign("coth", DefaultFunctions.HYPERBOLIC_COTANGENT.get());

        assign("asin", DefaultFunctions.ARCSINE.get());
        assign("acos", DefaultFunctions.ARCCOSINE.get());
        assign("atan", DefaultFunctions.ARCTANGENT.get());
        assign("acsc", DefaultFunctions.ARCCOSECANT.get());
        assign("asec", DefaultFunctions.ARCSECANT.get());
        assign("acot", DefaultFunctions.ARCCOTANGENT.get());
        assign("atan2", DefaultFunctions.ARCTANGENT2.get());

        assign("forEach", DefaultFunctions.FOREACH.get());
        assign("each", DefaultFunctions.FOREACH.get());
        assign("map", DefaultFunctions.MAP.get());
        assign("filter", DefaultFunctions.FILTER.get());
        assign("fold", DefaultFunctions.FOLD.get());

        // Types
        assign("undefined", Undefined.VALUE);
    }
}
