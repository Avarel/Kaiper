package xyz.avarel.kaiper.vm.executor;

import xyz.avarel.kaiper.bytecode.io.ByteInput;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.bytecode.reader.consumers.PatternOpcodeConsumerAdapter;
import xyz.avarel.kaiper.bytecode.reader.consumers.ReadResult;
import xyz.avarel.kaiper.exceptions.InvalidBytecodeException;
import xyz.avarel.kaiper.vm.patterns.*;
import xyz.avarel.kaiper.vm.utils.VMStack;

import java.util.LinkedList;
import java.util.List;

import static xyz.avarel.kaiper.bytecode.reader.consumers.ReadResult.CONTINUE;
import static xyz.avarel.kaiper.bytecode.reader.consumers.ReadResult.ENDED;

public class PatternCreator extends PatternOpcodeConsumerAdapter {
    public StackMachine parent;
    public VMStack<Pattern> pStack = new VMStack<>();

    public PatternCreator(StackMachine parent) {
        this.parent = parent;
    }

    @Override
    public ReadResult accept(OpcodeReader reader, Opcode opcode, ByteInput in) {
        parent.checkTimeout();
        ReadResult result = super.accept(reader, opcode, in);
        parent.checkTimeout();
        return result;
    }

    @Override
    public ReadResult opcodeEnd(OpcodeReader reader, ByteInput in) {
        short endId = in.readShort();

        if (endId != parent.depth - 1) {
            throw new InvalidBytecodeException("Unexpected End " + endId);
        }
        return ENDED;
    }

    @Override
    public ReadResult opcodePatternCase(OpcodeReader reader, ByteInput in) {
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
    public ReadResult opcodeWildcardPattern(OpcodeReader reader, ByteInput in) {
        pStack.push(WildcardPattern.INSTANCE);
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeVariablePattern(OpcodeReader reader, ByteInput in) {
        pStack.push(new VariablePattern(parent.stringPool.get(in.readUnsignedShort())));
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeTuplePattern(OpcodeReader reader, ByteInput in) {
        String name = parent.stringPool.get(in.readUnsignedShort());

        parent.depth++;
        reader.read(this, in);
        parent.depth--;

        pStack.push(new TuplePattern(name, pStack.pop()));

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeRestPattern(OpcodeReader reader, ByteInput in) {
        pStack.push(new RestPattern(parent.stringPool.get(in.readUnsignedShort())));
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeValuePattern(OpcodeReader reader, ByteInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeDefaultPattern(OpcodeReader reader, ByteInput in) {
        return CONTINUE;
    }
}
