package xyz.avarel.kaiper.bytecode.reader.consumers.impl;

import xyz.avarel.kaiper.bytecode.io.KDataInput;
import xyz.avarel.kaiper.bytecode.io.KDataOutput;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.bytecode.reader.consumers.PatternOpcodeProcessorAdapter;
import xyz.avarel.kaiper.bytecode.reader.consumers.ReadResult;
import xyz.avarel.kaiper.exceptions.InvalidBytecodeException;

import static xyz.avarel.kaiper.bytecode.reader.consumers.ReadResult.CONTINUE;
import static xyz.avarel.kaiper.bytecode.reader.consumers.ReadResult.ENDED;

public class PatternOpcodeBufferProcessor extends PatternOpcodeProcessorAdapter {
    private final KDataOutput out;
    private int depth = 0;

    public PatternOpcodeBufferProcessor(KDataOutput out) {
        this.out = out;
    }

    public PatternOpcodeBufferProcessor(KDataOutput out, int depth) {
        this.out = out;
        this.depth = depth;
    }

    @Override
    public ReadResult process(OpcodeReader reader, Opcode opcode, KDataInput in) {
        out.writeOpcode(opcode);
        return super.process(reader, opcode, in);
    }

    @Override
    public ReadResult opcodeEnd(OpcodeReader reader, KDataInput in) {
        short endId = in.readShort();
        out.writeShort(endId);

        if (endId != (depth - 1)) {
            throw new InvalidBytecodeException("Unexpected End " + endId);
        }

        return ENDED;
    }

    @Override
    public ReadResult opcodePatternCase(OpcodeReader reader, KDataInput in) {
        depth++;
        reader.read(this, in);
        depth--;

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeWildcardPattern(OpcodeReader reader, KDataInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeVariablePattern(OpcodeReader reader, KDataInput in) {
        out.writeShort(in.readShort());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeTuplePattern(OpcodeReader reader, KDataInput in) {
        out.writeShort(in.readShort());

        depth++;
        reader.read(this, in);
        depth--;

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeRestPattern(OpcodeReader reader, KDataInput in) {
        out.writeShort(in.readShort());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeValuePattern(OpcodeReader reader, KDataInput in) {
        depth++;
        reader.read(new KOpcodeBufferProcessor(out, depth), in);
        depth--;

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeDefaultPattern(OpcodeReader reader, KDataInput in) {
        depth++;
        reader.read(this, in);
        reader.read(new KOpcodeBufferProcessor(out, depth), in);
        depth--;

        return CONTINUE;
    }
}
