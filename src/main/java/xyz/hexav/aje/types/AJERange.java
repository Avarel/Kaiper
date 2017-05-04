package xyz.hexav.aje.types;

public class AJERange extends AJEList {
    private final AJEValue start;
    private final AJEValue end;
    private final AJEValue c;

    public AJERange(AJEValue start, AJEValue end, AJEValue c) {
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
                before = c != null && !(c instanceof AJERange) ? (int) c.value() : init - 1,
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
