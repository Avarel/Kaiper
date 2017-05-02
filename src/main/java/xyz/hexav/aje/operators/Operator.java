package xyz.hexav.aje.operators;

import xyz.hexav.aje.expressions.Evaluable;
import xyz.hexav.aje.expressions.Expression;

public class Operator {
    public final String symbol;
    public final int args;
    public final int next; // Next precedence
    private final Evaluable evaluator;

    public Operator(String symbol, int args) {
        this(symbol, args, null);
    }

    public Operator(String symbol, int args, Evaluable evaluator) {
        this(symbol, args, -1, evaluator);
    }

    public Operator(String symbol, int args, int next, Evaluable evaluator) {
        this.symbol = symbol;
        this.args = args;
        this.next = next;
        this.evaluator = evaluator;
    }

    @Override
    public String toString() {
        return "Op(" + symbol + ", " + args + ")";
    }

    public double eval(double... args) {
        return evaluator.eval(args);
    }

    public Expression compile(Expression a, Expression b) {
        return () ->
        {
            final double[] _a = a.evalList(), _b = b.evalList();

            if (_a.length == 1 && _b.length == 1)
                return new double[]{eval(_a[0], _b[0])};

            final int length = _a.length == 1
                    ? _b.length : _b.length == 1
                    ? _a.length : Math.min(_a.length, _b.length);

            final double[] result = new double[length];

            for (int i = 0; i < length; i++) {
                result[i] = eval(_a[i % _a.length], _b[i % _b.length]);
            }

            return result;
        };
    }

    public Expression compile(Expression exp) {
        return () ->
        {
            double[] parse = exp.evalList();

            for (int i = 0; i < parse.length; i++) {
                parse[i] = eval(parse[i]);
            }

            return parse;
        };
    }
}
