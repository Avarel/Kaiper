package xyz.avarel.kaiper.vm.patterns;


import xyz.avarel.kaiper.vm.compiled.CompiledScopedExecution;

// literally literals
public class ValuePattern implements Pattern {
    private final CompiledScopedExecution value;

    public ValuePattern(CompiledScopedExecution value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public CompiledScopedExecution getValue() {
        return value;
    }
}
