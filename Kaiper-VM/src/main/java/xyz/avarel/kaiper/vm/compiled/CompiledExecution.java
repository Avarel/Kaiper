package xyz.avarel.kaiper.vm.compiled;

import xyz.avarel.kaiper.bytecode.walker.BytecodeBatchReader;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.Scope;
import xyz.avarel.kaiper.vm.StackMachineWalker;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

public class CompiledExecution {
    private final byte[] bytecode;
    private final BytecodeBatchReader reader;
    private final List<String> stringPool;
    private final int depth;

    public CompiledExecution(byte[] bytecode, BytecodeBatchReader reader, List<String> stringPool, int depth) {
        this.bytecode = bytecode;
        this.reader = reader;
        this.stringPool = stringPool;
        this.depth = depth;
    }

    public Obj executeWithScope(Scope scope) throws IOException {
        StackMachineWalker walker = new StackMachineWalker(scope);
        reader.walkInsts(new DataInputStream(new ByteArrayInputStream(bytecode)), walker, stringPool, depth);
        return walker.getStack().pop();
    }
}
