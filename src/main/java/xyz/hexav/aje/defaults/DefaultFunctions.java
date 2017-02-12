package xyz.hexav.aje.defaults;

import xyz.hexav.aje.Function;

public enum DefaultFunctions {
    ABSOLUTE_VALUE(new NativeFunction("abs", 1, args -> Math.abs(args[0]))),
    SQUARE_ROOT(new NativeFunction("sqrt", 1, args -> Math.sqrt(args[0]))),
    CUBE_ROOT(new NativeFunction("cbrt", 1, args -> Math.cbrt(args[0]))),

    FLOOR(new NativeFunction("floor", 1, args -> Math.floor(args[0]))),
    CEILING(new NativeFunction("ceil", 1, args -> Math.ceil(args[0]))),
    ROUND(new NativeFunction("round", 1, args -> Math.round(args[0]))),
    SIGN(new NativeFunction("signum", 1, args -> Math.signum(args[0]))),

    MAX(new NativeFunction("max", 2, args -> Math.max(args[0], args[1]))),
    MIN(new NativeFunction("min", 2, args -> Math.min(args[0], args[1]))),

    LOG_NATURAL(new NativeFunction("ln", 1, args -> Math.log(args[0]))),
    LOG_10(new NativeFunction("log", 1, args -> Math.log10(args[0]))),
    EXPONENTIAL(new NativeFunction("exp", 1, args -> Math.exp(args[0]))),

    SINE(new NativeFunction("sin", 1, args -> Math.sin(args[0]))),
    COSINE(new NativeFunction("cos", 1, args -> Math.cos(args[0]))),
    TANGENT(new NativeFunction("tan", 1, args -> Math.tan(args[0]))),
    ARCSINE(new NativeFunction("asin", 1, args -> Math.asin(args[0]))),
    ARCCOSINE(new NativeFunction("acos", 1, args -> Math.acos(args[0]))),
    ARCTANGENT(new NativeFunction("atan", 1, args -> Math.atan(args[0]))),
    ARCTANGENT2(new NativeFunction("atan2", 2, args -> Math.atan2(args[0], args[1]))),

    HYPERBOLIC_SINE(new NativeFunction("sinh", 1, args -> Math.sinh(args[0]))),
    HYPERBOLIC_COSINE(new NativeFunction("cosh", 1, args -> Math.cosh(args[0]))),
    HYPERBOLIC_TANGENT(new NativeFunction("tanh", 1, args -> Math.tanh(args[0]))),

    LIST_SIZE(new NativeFunction("size", 0) {
        @Override
        public double[] evalList() {
            return new double[]{inputs.length};
        }
    }),

    SUMMATION(new NativeFunction("sum", 0, args -> {
        double sum = 0;
        for (double i : args) sum += i;
        return sum;
    })),
    AVERAGE(new NativeFunction("avg", 0, args -> {
        double sum = 0;
        for (double i : args) sum += i;
        return sum / args.length;
    })),
    PRODUCT(new NativeFunction("prod", 0, args -> {
        double sum = args[0];
        for (int i = 1; i < args.length; i++) sum *= args[i];
        return sum;
    })),

    QUADRATIC_ROOT(new NativeFunction("quadrt", 3) {
        @Override
        public double[] evalList() {
            double[] args = inputs;
            double[] ans = new double[2];
            ans[0] = (-args[1] + Math.sqrt(Math.pow(args[1], 2) - (4 * args[0] * args[2]))) / (2 * args[0]);
            ans[1] = (-args[1] - Math.sqrt(Math.pow(args[1], 2) - (4 * args[0] * args[2]))) / (2 * args[0]);
            return ans;
        }
    }),;

    private final Function function;

    DefaultFunctions(Function function) {
        this.function = function;
    }

    public Function get() {
        return function;
    }
}
