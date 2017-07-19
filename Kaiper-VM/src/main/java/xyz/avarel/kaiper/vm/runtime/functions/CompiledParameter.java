package xyz.avarel.kaiper.vm.runtime.functions;

import xyz.avarel.kaiper.runtime.functions.Parameter;
import xyz.avarel.kaiper.vm.compiled.CompiledScopedExecution;

public class CompiledParameter extends Parameter {
    private final CompiledScopedExecution defaultValue;

    public CompiledParameter(String name, CompiledScopedExecution defaultValue, boolean rest) {
        super(name,rest);
        this.defaultValue = defaultValue;
    }

    public boolean hasDefault() {
        return defaultValue != null;
    }

    public CompiledScopedExecution getDefaultValue() {
        return defaultValue;
    }
}
