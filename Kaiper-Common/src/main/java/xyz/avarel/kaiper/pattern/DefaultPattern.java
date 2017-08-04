package xyz.avarel.kaiper.pattern;

// (delegate) = (defaultExpr)
public class DefaultPattern<T> implements Pattern {
    private final NamedPattern delegate;
    private final T defaultExpr;

    public DefaultPattern(NamedPattern delegate, T defaultExpr) {
        this.delegate = delegate;
        this.defaultExpr = defaultExpr;
    }

    public NamedPattern getDelegate() {
        return delegate;
    }

    public T getDefaultExpr() {
        return defaultExpr;
    }

    @Override
    public <R, C> R accept(PatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return delegate + " = " + defaultExpr;
    }
}
