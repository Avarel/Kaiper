package xyz.avarel.kaiper.vm.compiled;

import xyz.avarel.kaiper.bytecode.io.KDataInputStream;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.runtime.Tuple;
import xyz.avarel.kaiper.vm.executor.StackMachine;

import java.io.ByteArrayInputStream;
import java.util.List;

public class PreparedPatternExecution {
    private final byte[] bytecode;
    private final int depth;
    private final List<String> stringPool;
    private final OpcodeReader reader;

    public PreparedPatternExecution(OpcodeReader reader, byte[] bytecode, int depth, List<String> stringPool) {
        this.bytecode = bytecode;
        this.depth = depth;
        this.stringPool = stringPool;
        this.reader = reader;
    }


    public boolean execute(StackMachine executor, Tuple tuple) {

        //save state
        int lastDepth = executor.depth;

        //load temp state
        executor.depth = this.depth;
        boolean result = executor.patternProcessor.assignFrom(tuple, new KDataInputStream(new ByteArrayInputStream(bytecode)));

        //load state
        executor.depth = lastDepth;


        return result;
    }
}
