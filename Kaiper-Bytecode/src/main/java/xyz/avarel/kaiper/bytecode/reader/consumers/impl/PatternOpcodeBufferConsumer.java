package xyz.avarel.kaiper.bytecode.reader.consumers.impl;

import xyz.avarel.kaiper.bytecode.io.ByteInput;
import xyz.avarel.kaiper.bytecode.io.ByteOutput;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.bytecode.reader.consumers.PatternOpcodeConsumerAdapter;
import xyz.avarel.kaiper.bytecode.reader.consumers.ReadResult;
import xyz.avarel.kaiper.exceptions.InvalidBytecodeException;

import static xyz.avarel.kaiper.bytecode.reader.consumers.ReadResult.CONTINUE;
import static xyz.avarel.kaiper.bytecode.reader.consumers.ReadResult.ENDED;

public class PatternOpcodeBufferConsumer extends PatternOpcodeConsumerAdapter {
    private final ByteOutput out;
    private int depth = 0;

    public PatternOpcodeBufferConsumer(ByteOutput out) {
        this.out = out;
    }

    public PatternOpcodeBufferConsumer(ByteOutput out, int depth) {
        this.out = out;
        this.depth = depth;
    }

    @Override
    public ReadResult accept(OpcodeReader reader, Opcode opcode, ByteInput in) {
        out.writeOpcode(opcode);
        return super.accept(reader, opcode, in);
    }

    @Override
    public ReadResult opcodeEnd(OpcodeReader reader, ByteInput in) {
        short endId = in.readShort();
        out.writeShort(endId);

        if (endId != (depth - 1)) {
            throw new InvalidBytecodeException("Unexpected End " + endId);
        }

        return ENDED;
    }

    @Override
    public ReadResult opcodePatternCase(OpcodeReader reader, ByteInput in) {
        depth++;
        reader.read(this, in);
        depth--;

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeWildcardPattern(OpcodeReader reader, ByteInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeVariablePattern(OpcodeReader reader, ByteInput in) {
        out.writeShort(in.readShort());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeTuplePattern(OpcodeReader reader, ByteInput in) {
        out.writeShort(in.readShort());

        depth++;
        reader.read(this, in);
        depth--;

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeRestPattern(OpcodeReader reader, ByteInput in) {
        out.writeShort(in.readShort());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeValuePattern(OpcodeReader reader, ByteInput in) {
        depth++;
        reader.read(new OpcodeBufferConsumer(out, depth), in);
        depth--;

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeDefaultPattern(OpcodeReader reader, ByteInput in) {
        depth++;
        reader.read(this, in);
        reader.read(new OpcodeBufferConsumer(out, depth), in);
        depth--;

        return CONTINUE;
    }
}
