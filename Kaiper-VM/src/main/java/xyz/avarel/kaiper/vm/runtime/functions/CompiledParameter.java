package xyz.avarel.kaiper.vm.runtime.functions;

import xyz.avarel.kaiper.runtime.functions.Parameter;
import xyz.avarel.kaiper.vm.compiled.CompiledExecution;

public class CompiledParameter extends Parameter {
    private final CompiledExecution defaultValue;

    public CompiledParameter(String name, CompiledExecution defaultValue, boolean rest) {
        super(name,rest);
        this.defaultValue = defaultValue;
    }

    public boolean hasDefault() {
        return defaultValue != null;
    }

    public CompiledExecution getDefaultValue() {
        return defaultValue;
    }
}
