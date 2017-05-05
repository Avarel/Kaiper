package xyz.hexav.aje.types;

import xyz.hexav.aje.defaults.DefaultOperators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class Slice extends ArrayList<Expression> implements Expression, Iterable<Expression> {
    private static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
    public static final Slice EMPTY = new Slice(EMPTY_DOUBLE_ARRAY);

    //protected final List<Expression> expressions;

    public Slice() {
        super();
    }

    public Slice(Expression... expressions) {
        super(Arrays.asList(expressions));
    }

    public Slice(double... expressions) {
        this();
        for (double value : expressions) {
            add(() -> value);
        }
    }

    public Slice(List<Expression> expressions) {
        super(expressions);
    }

    @Override
    public double value() {
        return values()[0];
    }

    public double[] values() {
        expand();

        double[] results = new double[size()];

        for (int i = 0; i < size(); i++) {
            results[i] = get(i).value();
        }

        return results;
    }

    // Basically when ranges/lists are put inside lists, they are lazily compiled as expressions.
    // The internal lists are then expanded and then added to the final list.
    protected void expand() {
        int i = 0;
        while (i < size()) {
            if (get(i) instanceof Slice) {
                Slice slice = (Slice) remove(i);
                slice.expand();
                addAll(i, slice);
            } else i++;
        }
    }

    @Override
    public String asString() {
        return toString();
    }

    public String toString() {
        return Arrays.stream(values())
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    @Override
    public Expression compile(BinaryOperator<Expression> operation, Expression number) {
        if (operation == DefaultOperators.LIST_INDEX) {
            return DefaultOperators.LIST_INDEX.apply(this, number);
        }
        return Slice.listOperation(this,
                operation,
                number instanceof Slice ? (Slice) number : new Slice(number));
    }

    public static Slice listOperation(Slice target, BinaryOperator<Expression> operation, Slice number) {
        int a = target.size();
        int b = number.size();

        if (a == 1 && b == 1)
            return new Slice(operation.apply(target, number));

        final int len;
        if (a == 1) len = b;
        else if (b == 1) len = a;
        else len = Math.min(a, b);

        Expression[] evaluators = new Expression[len];

        for (int i = 0; i < len; i++) {
            evaluators[i] = operation.apply(target.get(i % a), number.get(i % b));
        }

        return new Slice(evaluators);
    }



    /// LIST IMPLEMENTATIONS

    public Expression get(Expression exp) {
        return () -> (get((int) exp.value())).value();
    }


    public Slice subList(Slice indices) { //FIXME fix for lazy ranges (need to expand() when getting the first one?)
        Slice slice = new Slice();
        for (Expression exp : indices) {
            slice.add(get(exp));
        }
        return slice;
    }
}
