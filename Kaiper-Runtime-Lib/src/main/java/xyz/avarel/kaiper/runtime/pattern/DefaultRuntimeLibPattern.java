package xyz.avarel.kaiper.runtime.pattern;

import xyz.avarel.kaiper.runtime.Obj;

// (delegate) = (defaultExpr)
public class DefaultRuntimeLibPattern implements RuntimeLibPattern {
    private final NamedRuntimeLibPattern delegate;
    private final Obj defaultObj;

    public DefaultRuntimeLibPattern(NamedRuntimeLibPattern delegate, Obj defaultObj) {
        this.delegate = delegate;
        this.defaultObj = defaultObj;
    }

    public NamedRuntimeLibPattern getDelegate() {
        return delegate;
    }

    public Obj getDefault() {
        return defaultObj;
    }

    @Override
    public <R, C> R accept(RuntimePatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return delegate + " = " + defaultObj;
    }
}
