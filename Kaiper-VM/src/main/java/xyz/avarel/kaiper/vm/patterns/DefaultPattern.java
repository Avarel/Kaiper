package xyz.avarel.kaiper.vm.patterns;

import xyz.avarel.kaiper.vm.compiled.CompiledScopedExecution;

// (delegate) = (defaultValue)
public class DefaultPattern implements Pattern {
    private final NamedPattern delegate;
    private final CompiledScopedExecution defaultValue;

    public DefaultPattern(NamedPattern delegate, CompiledScopedExecution defaultValue) {
        this.delegate = delegate;
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return delegate + " = " + defaultValue;
    }

    public NamedPattern getDelegate() {
        return delegate;
    }

    public CompiledScopedExecution getDefault() {
        return defaultValue;
    }
}
