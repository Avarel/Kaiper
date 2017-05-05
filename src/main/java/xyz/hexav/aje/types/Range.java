package xyz.hexav.aje.types;

public class Range extends Slice {
    private final Expression start;
    private final Expression end;

    public Range(Expression start, Expression end, Expression c) {
        super();
        this.start = start;
        this.end = end;
    }

    @Override
    public Expression get(int i) {
        return () -> {
            int start = (int) this.start.value();
            int end = (int) this.end.value();

            if (start > i || i > end) {
                throw new IndexOutOfBoundsException();
            }

            return i + start;
        };
    }

    @Override
    public boolean add(Expression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int i, Expression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Expression remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }
}
