package xyz.avarel.kaiper.pattern;


// literally literals
public class ValuePattern<T> implements Pattern {
    private final T value;

    public ValuePattern(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public <R, C> R accept(PatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
