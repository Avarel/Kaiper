package xyz.avarel.kaiper.vm.compiled;

import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.Scope;
import xyz.avarel.kaiper.vm.executor.StackMachine;

public class CompiledScopedExecution extends CompiledExecution {
    private final Scope baseScope;

    public CompiledScopedExecution(byte[] bytecode, String[] stringPool, Scope baseScope) {
        super(bytecode, stringPool);
        this.baseScope = baseScope;
    }

    public Obj execute() {
        return execute(baseScope.subPool());
    }

    public Obj execute(StackMachine executor) {
        return execute(executor, baseScope.subPool());
    }
}
