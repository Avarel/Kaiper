package xyz.avarel.kaiper.vm.patterns;

import xyz.avarel.kaiper.vm.compiled.CompiledScopedExecution;

// (delegate) = (defaultExpr)
public class DefaultPattern implements Pattern {
    private final NamedPattern delegate;
    private final CompiledScopedExecution defaultExpr;

    public DefaultPattern(NamedPattern delegate, CompiledScopedExecution defaultExpr) {
        this.delegate = delegate;
        this.defaultExpr = defaultExpr;
    }

    @Override
    public String toString() {
        return delegate + " = " + defaultExpr;
    }

    public NamedPattern getDelegate() {
        return delegate;
    }

    public CompiledScopedExecution getDefault() {
        return defaultExpr;
    }
}
