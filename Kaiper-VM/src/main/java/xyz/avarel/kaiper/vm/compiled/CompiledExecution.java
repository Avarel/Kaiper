package xyz.avarel.kaiper.vm.compiled;

import xyz.avarel.kaiper.bytecode.io.KDataInputStream;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.Scope;
import xyz.avarel.kaiper.vm.executor.StackMachine;

import java.io.ByteArrayInputStream;
import java.util.List;

public class CompiledExecution {
    private final byte[] bytecode;
    private final int depth;
    private final List<String> stringPool;
    private final OpcodeReader reader;
    private final Object lock_defaultExecutor = new Object();
    private StackMachine defaultExecutor;

    public CompiledExecution(OpcodeReader reader, byte[] bytecode, int depth, List<String> stringPool) {
        this.bytecode = bytecode;
        this.depth = depth;
        this.stringPool = stringPool;
        this.reader = reader;
    }

    public Obj execute(Scope scope) {
        //Lazy and recycling executors
        StackMachine executor;
        synchronized (lock_defaultExecutor) {
            if (this.defaultExecutor == null) {
                executor = new StackMachine(scope, stringPool);
            } else {
                executor = this.defaultExecutor;
                this.defaultExecutor = null;
            }
        }


        //reset
        executor.scope = scope;
        executor.stack.unlock();
        executor.stack.clear();
        executor.depth = depth;
        executor.lineNumber = -1;
        executor.resetTimeout();

        reader.read(executor, new KDataInputStream(new ByteArrayInputStream(bytecode)));
        Obj result = executor.stack.pop();

        //remove any possible references
        executor.scope = null;
        executor.stack.unlock();
        executor.stack.clear();


        synchronized (lock_defaultExecutor) {
            this.defaultExecutor = executor;
        }

        return result;
    }

    public Obj execute(StackMachine executor, Scope scope) {
        if (executor == null) {
            return execute(scope);
        }

        //save state
        Scope lastScope = executor.scope;
        int lastLock = executor.stack.lock();
        int lastDepth = executor.depth;
        long lastLn = executor.lineNumber;

        //load temp state
        executor.scope = scope;
        executor.depth = this.depth;
        executor.stack.setLock(lastLock);
        executor.lineNumber = -1;

        reader.read(executor, new KDataInputStream(new ByteArrayInputStream(bytecode)));
        Obj result = executor.stack.pop();

        //load state
        executor.depth = lastDepth;
        executor.scope = lastScope;
        executor.lineNumber = lastLn;
        executor.stack.popToLock();
        executor.stack.setLock(lastLock);

        return result;
    }
}
