package xyz.hexav.aje.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AJEList implements AJEValue {
    private static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
    public static final AJEList EMPTY = new AJEList(EMPTY_DOUBLE_ARRAY);

    protected final List<AJEValue> values;

    public AJEList() {
        this.values = new ArrayList<>();
    }

    public AJEList(List<AJEValue> values) {
        this.values = values;
    }

    public AJEList(AJEValue... values) {
        this.values = Arrays.asList(values);
    }

    public AJEList(double... values) {
        this.values = Arrays.stream(values)
                .mapToObj(AJEValue::ofValue)
                .collect(Collectors.toList());
    }

    public AJEValue get(int i) {
        return values.get(i);
    }

    public int size() {
        return values.size();
    }

    @Override
    public double value() {
        return values()[0];
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public void add(AJEValue value) {
        values.add(value);
    }

    public void add(AJEList list) {
        if (list instanceof AJERange) {
            AJERange range = (AJERange) list;
            range.setup();
        }
        values.addAll(list.values);
    }

    public double[] values() {
        double[] results = new double[size()];

        for (int i = 0; i < size(); i++) {
            results[i] = get(i).value();
        }

        return results;
    }

    public String asString() {
        return Arrays.stream(values())
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
