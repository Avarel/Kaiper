package xyz.avarel.kaiper.vm.compiled;

import xyz.avarel.kaiper.bytecode.walker.BytecodeBatchReader;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.Scope;

import java.io.IOException;
import java.util.List;

public class CompiledScopedExecution {
    private final CompiledExecution execution;
    private final Scope baseScope;

    public CompiledScopedExecution(byte[] bytecode, BytecodeBatchReader reader, List<String> stringPool, int depth, Scope baseScope) {
        this.execution = new CompiledExecution(bytecode, reader, stringPool, depth);
        this.baseScope = baseScope.copy();
    }

    public Obj execute() throws IOException {
        return execution.executeWithScope(baseScope.subPool());
    }
}
