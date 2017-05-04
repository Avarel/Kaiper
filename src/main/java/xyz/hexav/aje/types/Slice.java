package xyz.hexav.aje.types;

import xyz.hexav.aje.defaults.DefaultOperators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class Slice implements Expression {
    private static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
    public static final Slice EMPTY = new Slice(EMPTY_DOUBLE_ARRAY);

    protected final List<Expression> values;

    public Slice() {
        this.values = new ArrayList<>();
    }

    public Slice(Expression... values) {
        this(Arrays.asList(values));
    }

    public Slice(double... values) {
        this();
        for (double value : values) {
            this.values.add(() -> value);
        }
    }

    public Slice(List<Expression> values) {
        this();
        this.values.addAll(values);
    }

    public Expression get(int i) {
        return values.get(i);
    }

    public int size() {
        return values.size();
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





    public void addAll(Expression value) {
        values.add(value);
    }

    public void addAll(Slice list) {
        if (list instanceof Range) {
            Range range = (Range) list;
            range.setup();
        }
        values.addAll(list.values);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public Expression compile(BinaryOperator<Expression> operation, Expression number) {
        if (operation == DefaultOperators.ITEM_AT_LIST.get()) {
            return DefaultOperators.ITEM_AT_LIST.get().apply(this, number);
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
}
