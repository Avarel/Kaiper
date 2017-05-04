package xyz.hexav.aje.defaults;

import xyz.hexav.aje.Function;

public enum DefaultFunctions {
    ABSOLUTE_VALUE(new NativeFunction("abs", 1, args -> Math.abs(args[0].value()))),
    SQUARE_ROOT(new NativeFunction("sqrt", 1, args -> Math.sqrt(args[0].value()))),
    CUBE_ROOT(new NativeFunction("cbrt", 1, args -> Math.cbrt(args[0].value()))),

    FLOOR(new NativeFunction("floor", 1, args -> Math.floor(args[0].value()))),
    CEILING(new NativeFunction("ceil", 1, args -> Math.ceil(args[0].value()))),
    ROUND(new NativeFunction("round", 1, args -> Math.round(args[0].value()))),
    SIGN(new NativeFunction("signum", 1, args -> Math.signum(args[0].value()))),

    MAX(new NativeFunction("max", 2, args -> Math.max(args[0].value(), args[1].value()))),
    MIN(new NativeFunction("min", 2, args -> Math.min(args[0].value(), args[1].value()))),

    LOG_NATURAL(new NativeFunction("ln", 1, args -> Math.log(args[0].value()))),
    LOG_10(new NativeFunction("log", 1, args -> Math.log10(args[0].value()))),
    EXPONENTIAL(new NativeFunction("exp", 1, args -> Math.exp(args[0].value()))),

    SINE(new NativeFunction("sin", 1, args -> Math.sin(args[0].value()))),
    COSINE(new NativeFunction("cos", 1, args -> Math.cos(args[0].value()))),
    TANGENT(new NativeFunction("tan", 1, args -> Math.tan(args[0].value()))),
    ARCSINE(new NativeFunction("asin", 1, args -> Math.asin(args[0].value()))),
    ARCCOSINE(new NativeFunction("acos", 1, args -> Math.acos(args[0].value()))),
    ARCTANGENT(new NativeFunction("atan", 1, args -> Math.atan(args[0].value()))),
    ARCTANGENT2(new NativeFunction("atan2", 2, args -> Math.atan2(args[0].value(), args[1].value()))),

    HYPERBOLIC_SINE(new NativeFunction("sinh", 1, args -> Math.sinh(args[0].value()))),
    HYPERBOLIC_COSINE(new NativeFunction("cosh", 1, args -> Math.cosh(args[0].value()))),
    HYPERBOLIC_TANGENT(new NativeFunction("tanh", 1, args -> Math.tanh(args[0].value()))),

//    LIST_SIZE(new NativeFunction("size", 0) {
//        @Override
//        public double[] evalList() {
//            return new double[]{inputs.length};
//        }
//    }),
    ;

//    SUMMATION(new NativeFunction("sum", 0, args -> {
//        double sum = 0;
//        for (double i : args) sum += i;
//        return sum;
//    })),
//    AVERAGE(new NativeFunction("avg", 0, args -> {
//        double sum = 0;
//        for (double i : args) sum += i;
//        return sum / args.length;
//    })),
//    PRODUCT(new NativeFunction("prod", 0, args -> {
//        double sum = args[0].eval();
//        for (int i = 1; i < args.length; i++) sum *= args[i];
//        return sum;
//    })),

    private final Function function;

    DefaultFunctions(Function function) {
        this.function = function;
    }

    public Function get() {
        return function;
    }
}
