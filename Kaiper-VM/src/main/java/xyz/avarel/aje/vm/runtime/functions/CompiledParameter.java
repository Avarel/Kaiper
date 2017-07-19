package xyz.avarel.aje.vm.runtime.functions;

import xyz.avarel.aje.runtime.functions.Parameter;
import xyz.avarel.aje.vm.compiled.CompiledScopedExecution;

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
