package xyz.avarel.kaiper.ast.pattern;

import xyz.avarel.kaiper.ast.Expr;

// (delegate) = (defaultExpr)
public class DefaultPattern implements Pattern {
    private final NamedPattern delegate;
    private final Expr defaultExpr;

    public DefaultPattern(NamedPattern delegate, Expr defaultExpr) {
        this.delegate = delegate;
        this.defaultExpr = defaultExpr;
    }

    public NamedPattern getDelegate() {
        return delegate;
    }

    public Expr getDefault() {
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
