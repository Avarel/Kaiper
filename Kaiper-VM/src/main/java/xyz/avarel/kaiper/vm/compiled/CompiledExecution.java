package xyz.avarel.kaiper.vm.compiled;

import xyz.avarel.kaiper.bytecode.io.ByteInputStream;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.Scope;
import xyz.avarel.kaiper.vm.executor.StackMachineConsumer;

import java.io.ByteArrayInputStream;
import java.util.List;

public class CompiledExecution {
    private final byte[] bytecode;
    private final int depth;
    private final List<String> stringPool;
    private final OpcodeReader reader;
    private StackMachineConsumer defaultExecutor;

    public CompiledExecution(OpcodeReader reader, byte[] bytecode, int depth, List<String> stringPool) {
        this.bytecode = bytecode;
        this.depth = depth;
        this.stringPool = stringPool;
        this.reader = reader;
    }

    public Obj execute(Scope scope) {
        //Lazy
        if (this.defaultExecutor == null) {
            this.defaultExecutor = new StackMachineConsumer(scope, stringPool);
        }

        StackMachineConsumer executor = this.defaultExecutor;

        //reset
        executor.scope = scope;
        executor.stack.unlock();
        executor.stack.clear();
        executor.depth = depth;
        executor.lineNumber = -1;
        executor.resetTimeout();

        reader.read(executor, new ByteInputStream(new ByteArrayInputStream(bytecode)));
        Obj result = executor.stack.pop();

        //remove any possible references
        executor.scope = null;
        executor.stack.unlock();
        executor.stack.clear();

        return result;
    }

    public Obj execute(StackMachineConsumer executor, Scope scope) {
        if (executor == null || executor == this.defaultExecutor) {
            return execute(scope);
        }

        //save state
        Scope lastScope = executor.scope;
        int lastLock = executor.stack.lock();
        int lastDepth = executor.depth;

        //load temp state
        executor.scope = scope;
        executor.depth = this.depth;
        executor.stack.setLock(lastLock);

        reader.read(executor, new ByteInputStream(new ByteArrayInputStream(bytecode)));
        Obj result = executor.stack.pop();

        //load state
        executor.depth = lastDepth;
        executor.scope = lastScope;
        executor.stack.popToLock();
        executor.stack.setLock(lastLock);

        return result;
    }
}
