package xyz.hexav.aje.operators;

import xyz.hexav.aje.expressions.Evaluable;
import xyz.hexav.aje.types.AJEList;
import xyz.hexav.aje.types.AJEValue;

import java.util.function.BiFunction;

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

    public AJEValue evalArgs(AJEValue... args) {
        return () -> evaluator.evalArgs(args);
    }

    public AJEValue compile(AJEValue a, AJEValue b) {
        if (a instanceof AJEList) {
            return operateOnList((AJEList) a, (a1, b2) -> evalArgs(a1, b2), b instanceof AJEList ? (AJEList) b : new AJEList(b));
        } else return evalArgs(a, b);
    }

    public AJEValue compile(AJEValue exp) {
        return evalArgs(exp);
    }

    private AJEList operateOnList(AJEList target, BiFunction<AJEValue, AJEValue, AJEValue> operation, AJEList number) {
        int a = target.size();
        int b = number.size();

        if (a == 1 && b == 1)
            return new AJEList(operation.apply(target, number));

        final int len;
        if (a == 1) len = b;
        else if (b == 1) len = a;
        else len = Math.min(a, b);

        AJEValue[] evaluators = new AJEValue[len];

        for (int i = 0; i < len; i++) {
            evaluators[i] = operation.apply(target.get(i % a), number.get(i % b));
        }

        return new AJEList(evaluators);
    }
}
