package xyz.hexav.aje.types;

public class Range extends Slice {
    private final Expression start;
    private final Expression end;
    private final Expression c;

    public Range(Expression start, Expression end, Expression c) {
        super();
        this.start = start;
        this.end = end;
        this.c = c;
    }

    public void setup() {
        if (!super.values.isEmpty()) return;

        final int
                init = (int) start.value(),
                end = (int) this.end.value(),
                before = c != null && !(c instanceof Range) ? (int) c.value() : init - 1,
                delta = Math.abs(init - before);

        if (init < end) {
            for (int i = init; i <= end; i += delta) {
                int finalI = i;
                super.values.add(() -> finalI);
            }
        } else {
            for (int i = init + 1; i >= end; i -= delta) {
                int finalI = i;
                super.values.add(() -> finalI);
            }
        }
    }

    @Override
    public double value() {
        setup();
        return super.value();
    }

    @Override
    public double[] values() {
        setup();
        return super.values();
    }
}
