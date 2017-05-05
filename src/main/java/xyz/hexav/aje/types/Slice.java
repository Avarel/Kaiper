package xyz.hexav.aje.types;

import xyz.hexav.aje.defaults.DefaultOperators;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Slice implements Expression, Iterable<Expression> {
    private static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
    public static final Slice EMPTY = new Slice(EMPTY_DOUBLE_ARRAY);

    protected final List<Expression> expressions;

    public Slice() {
        this.expressions = new ArrayList<>();
    }

    public Slice(Expression... expressions) {
        this(Arrays.asList(expressions));
    }

    public Slice(double... expressions) {
        this();
        for (double value : expressions) {
            this.expressions.add(() -> value);
        }
    }

    public Slice(List<Expression> expressions) {
        this();
        this.expressions.addAll(expressions);
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
                Slice slice = (Slice) expressions.remove(i);
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

    public Expression get(int i) {
        return expressions.get(i);
    }

    public Expression get(Expression exp) {
        return () -> (expressions.get((int) exp.value())).value();
    }


    public int size() {
        return expressions.size();
    }

    public Expression subslice(Slice indices) { //FIXME fix for lazy ranges (need to expand() when getting the first one?)
        Slice slice = new Slice();
        for (Expression exp : indices) {
            slice.add(get(exp));
        }
        return slice;
    }

    public void add(Expression value) {
        expressions.add(value);
    }

    public void addAll(int i, Slice list) {
        expressions.addAll(i, list.expressions);
    }

    @Override
    public Iterator<Expression> iterator() {
        return expressions.iterator();
    }

    @Override
    public void forEach(Consumer<? super Expression> action) {
        expressions.forEach(action);
    }

    @Override
    public Spliterator<Expression> spliterator() {
        return expressions.spliterator();
    }



    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
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
}
