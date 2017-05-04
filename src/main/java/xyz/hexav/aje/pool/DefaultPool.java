package xyz.hexav.aje.pool;

import xyz.hexav.aje.defaults.DefaultFunctions;

public class DefaultPool extends Pool {
    public static DefaultPool INSTANCE = new DefaultPool();
    
    private DefaultPool() {
        allocVal("pi").assign(Math.PI);
        allocVal("e").assign(Math.E);

        allocFunc(DefaultFunctions.ABSOLUTE_VALUE.get());
        allocFunc(DefaultFunctions.SQUARE_ROOT.get());
        allocFunc(DefaultFunctions.CUBE_ROOT.get());

        allocFunc(DefaultFunctions.FLOOR.get());
        allocFunc(DefaultFunctions.CEILING.get());
        allocFunc(DefaultFunctions.ROUND.get());
        allocFunc(DefaultFunctions.SIGN.get());

        allocFunc(DefaultFunctions.MAX.get());
        allocFunc(DefaultFunctions.MIN.get());

        allocFunc(DefaultFunctions.LOG_NATURAL.get());
        allocFunc(DefaultFunctions.LOG_10.get());
        allocFunc(DefaultFunctions.EXPONENTIAL.get());

        allocFunc(DefaultFunctions.SINE.get());
        allocFunc(DefaultFunctions.COSINE.get());
        allocFunc(DefaultFunctions.TANGENT.get());
        allocFunc(DefaultFunctions.ARCSINE.get());
        allocFunc(DefaultFunctions.ARCCOSINE.get());
        allocFunc(DefaultFunctions.ARCTANGENT.get());
        allocFunc(DefaultFunctions.ARCTANGENT2.get());

        allocFunc(DefaultFunctions.HYPERBOLIC_SINE.get());
        allocFunc(DefaultFunctions.HYPERBOLIC_COSINE.get());
        allocFunc(DefaultFunctions.HYPERBOLIC_TANGENT.get());
    }
}
