package xyz.hexav.aje.types;

public class Range extends Slice {
    private final Expression start;
    private final Expression end;
    private boolean expanded;

    public Range(Expression start, Expression end, Expression c) {
        super();
        this.start = start;
        this.end = end;
    }

    @Override
    protected void expand() {
        if (expanded) return;

        System.out.println("range setting up");

        int init = (int) start.value();
        int end = (int) this.end.value();

        if (init < end) {
            for (int i = init; i <= end; i++) {
                add(new NumericValue(i));
            }
        } else {
            for (int i = init + 1; i >= end; i++) {
                add(new NumericValue(i));
            }
        }

        super.expand();
        expanded = true;
    }

    @Override
    public double value() {
        expand();
        return super.value();
    }

    @Override
    public double[] values() {
        expand();
        return super.values();
    }
}
