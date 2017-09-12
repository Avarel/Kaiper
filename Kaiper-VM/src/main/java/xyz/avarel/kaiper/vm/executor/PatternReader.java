package xyz.avarel.kaiper.vm.executor;

import xyz.avarel.kaiper.bytecode.io.KDataInput;
import xyz.avarel.kaiper.bytecode.opcodes.KOpcodes;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.bytecode.reader.processors.PatternOpcodeProcessorAdapter;
import xyz.avarel.kaiper.bytecode.reader.processors.ReadResult;
import xyz.avarel.kaiper.exceptions.InvalidBytecodeException;
import xyz.avarel.kaiper.vm.compiled.CompiledScopedExecution;
import xyz.avarel.kaiper.vm.patterns.*;
import xyz.avarel.kaiper.vm.utils.VMStack;

import java.util.LinkedList;
import java.util.List;

import static xyz.avarel.kaiper.bytecode.reader.processors.ReadResult.CONTINUE;
import static xyz.avarel.kaiper.bytecode.reader.processors.ReadResult.ENDED;

public class PatternReader extends PatternOpcodeProcessorAdapter {
    public StackMachine parent;
    public VMStack<Pattern> pStack = new VMStack<>();

    public PatternReader(StackMachine parent) {
        this.parent = parent;
    }

    @Override
    public ReadResult process(OpcodeReader reader, Opcode opcode, KDataInput in) {
        parent.checkTimeout();
        ReadResult result = super.process(reader, opcode, in);
        parent.checkTimeout();
        return result;
    }

    @Override
    public ReadResult opcodeEnd(OpcodeReader reader, KDataInput in) {
        int endId = in.readUnsignedShort();

        if (endId != parent.depth - 1) {
            throw new InvalidBytecodeException("Unexpected End " + endId);
        }
        return ENDED;
    }

    @Override
    public ReadResult opcodePatternCase(OpcodeReader reader, KDataInput in) {
        List<Pattern> patterns = new LinkedList<>();

        int lastLock = pStack.lock();

        parent.depth++;
        reader.read(this, in);
        parent.depth--;

        while (pStack.canPop()) {
            patterns.add(0, pStack.pop());
        }

        pStack.setLock(lastLock);

        pStack.push(new PatternCase(patterns));

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeWildcardPattern(OpcodeReader reader, KDataInput in) {
        pStack.push(WildcardPattern.INSTANCE);
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeVariablePattern(OpcodeReader reader, KDataInput in) {
        pStack.push(new VariablePattern(parent.stringPool.get(in.readUnsignedShort())));
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeTuplePattern(OpcodeReader reader, KDataInput in) {
        String name = parent.stringPool.get(in.readUnsignedShort());

        parent.depth++;
        reader.read(this, in);
        parent.depth--;

        pStack.push(new TuplePattern(name, pStack.pop()));

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeRestPattern(OpcodeReader reader, KDataInput in) {
        pStack.push(new RestPattern(parent.stringPool.get(in.readUnsignedShort())));
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeValuePattern(OpcodeReader reader, KDataInput in) {
        parent.depth++;

        int lastLock = pStack.lock();

        parent.byteBuffer.reset();
        reader.read(parent.buffer.reset(parent.byteBufferOutput, parent.depth), in);

        pStack.setLock(lastLock);

        pStack.push(new ValuePattern(
                new CompiledScopedExecution(
                        KOpcodes.READER,
                        parent.byteBuffer.toByteArray(),
                        parent.depth,
                        parent.stringPool,
                        parent.scope.subPool()
                )
        ));

        parent.depth--;


        return CONTINUE;
    }

    @Override
    public ReadResult opcodeDefaultPattern(OpcodeReader reader, KDataInput in) {
        parent.depth++;

        int lastLock = pStack.lock();

        reader.read(this, in);

        parent.byteBuffer.reset();
        reader.read(parent.buffer.reset(parent.byteBufferOutput, parent.depth), in);

        pStack.setLock(lastLock);

        pStack.push(new DefaultPattern(
                (NamedPattern) pStack.pop(),
                new CompiledScopedExecution(
                        KOpcodes.READER,
                        parent.byteBuffer.toByteArray(),
                        parent.depth,
                        parent.stringPool,
                        parent.scope.subPool()
                )
        ));

        parent.depth--;

        return CONTINUE;
    }
}
