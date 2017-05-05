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

    private int[] mapping;

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
        double[] results = new double[size()];

        for (int i = 0; i < size(); i++) {
            results[i] = get(i).value();
        }

        return results;
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

    private int[] calculateIndices() {
        if (mapping != null) return mapping;

        mapping = new int[size()];

        int k = 0;
        for (int i = 0; i < super.size(); i++) {
            Expression e = super.get(i);
            if (e instanceof Slice) {
                Slice slice = ((Slice) e);
                for (int j = 0; j < slice.size(); j++) {
                    mapping[k] = i;
                    k++;
                }
            } else {
                mapping[k] = i;
                k++;
            }
        }

        System.out.println(Arrays.toString(mapping));

        return mapping;
    }

    @Override
    public int size() {
        int size = 0;
        for (Expression e : this) {
            if (e instanceof Slice) {
                size += ((Slice) e).size();
            } else size++;
        }
        return size;
    }

    @Override
    public boolean add(Expression expression) {
        mapping = null;
        return super.add(expression);
    }

    @Override
    public void add(int i, Expression expression) {
        mapping = null;
        super.add(i, expression);
    }

    @Override
    public Expression remove(int i) {
        mapping = null;
        return super.remove(i);
    }

    @Override
    public Expression get(int i) {
        calculateIndices();

        Expression e = super.get(mapping[i]);

        if (e instanceof Slice) {
            Slice slice = ((Slice) e);
            return slice.get(i - mapping[i]);
        }

        return e;
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
