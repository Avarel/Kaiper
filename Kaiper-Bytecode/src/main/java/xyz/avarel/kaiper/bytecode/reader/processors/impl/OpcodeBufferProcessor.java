package xyz.avarel.kaiper.bytecode.reader.processors.impl;

import xyz.avarel.kaiper.bytecode.io.KDataInput;
import xyz.avarel.kaiper.bytecode.io.KDataOutput;
import xyz.avarel.kaiper.bytecode.opcodes.KOpcodes;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.opcodes.PatternOpcodes;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.bytecode.reader.processors.MultiOpcodeProcessorAdapter;
import xyz.avarel.kaiper.bytecode.reader.processors.ReadResult;

import static xyz.avarel.kaiper.bytecode.reader.processors.ReadResult.*;

public class OpcodeBufferProcessor extends MultiOpcodeProcessorAdapter {
    private KDataOutput out;

    public OpcodeBufferProcessor() {
        this(null);
    }

    public OpcodeBufferProcessor(KDataOutput out) {
        this.out = out;
    }

    @Override
    public ReadResult process(OpcodeReader reader, Opcode opcode, KDataInput in) {
        out.writeOpcode(opcode);
        return super.process(reader, opcode, in);
    }

    @Override
    public ReadResult opcodeEnd(OpcodeReader reader, KDataInput in) {
        return ENDED;
    }

    @Override
    public ReadResult opcodeLineNumber(OpcodeReader reader, KDataInput in) {
        out.writeLong(in.readLong());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeBreakpoint(OpcodeReader reader, KDataInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeReturn(OpcodeReader reader, KDataInput in) {
        return RETURNED;
    }

    @Override
    public ReadResult opcodeDup(OpcodeReader reader, KDataInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodePop(OpcodeReader reader, KDataInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNullConstant(OpcodeReader reader, KDataInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeBooleanConstant(OpcodeReader reader, boolean value, KDataInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeIntConstant(OpcodeReader reader, KDataInput in) {
        out.writeInt(in.readInt());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeDecimalConstant(OpcodeReader reader, KDataInput in) {
        out.writeDouble(in.readDouble());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeStringConstant(OpcodeReader reader, KDataInput in) {
        out.writeShort(in.readShort());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewArray(OpcodeReader reader, KDataInput in) {
        out.writeInt(in.readInt());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewDictionary(OpcodeReader reader, KDataInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewRange(OpcodeReader reader, KDataInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewFunction(OpcodeReader reader, KDataInput in) {
        out.writeShort(in.readShort());
        
        PatternOpcodes.READER.read(this, in);
        reader.read(this, in);

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewModule(OpcodeReader reader, KDataInput in) {
        out.writeShort(in.readShort());

        reader.read(this, in);

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewType(OpcodeReader reader, KDataInput in) {
        out.writeShort(in.readShort());

        PatternOpcodes.READER.read(this, in);
        reader.read(this, in);

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewTuple(OpcodeReader reader, KDataInput in) {
        int size = in.readInt();
        out.writeInt(size);

        for (int i = 0; i < size; i++) {
            out.writeShort(in.readShort());
            reader.read(this, in);
        }

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeDeclare(OpcodeReader reader, KDataInput in) {
        out.writeShort(in.readShort());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeAssign(OpcodeReader reader, KDataInput in) {
        out.writeBoolean(in.readBoolean()).writeShort(in.readShort());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeIdentifier(OpcodeReader reader, KDataInput in) {
        out.writeBoolean(in.readBoolean()).writeShort(in.readShort());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeBindDeclare(OpcodeReader reader, KDataInput in) {
        PatternOpcodes.READER.read(this, in);

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeBindAssign(OpcodeReader reader, KDataInput in) {
        PatternOpcodes.READER.read(this, in);

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeInvoke(OpcodeReader reader, KDataInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeUnaryOperation(OpcodeReader reader, KDataInput in) {
        out.writeByte(in.readByte());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeBinaryOperation(OpcodeReader reader, KDataInput in) {
        out.writeByte(in.readByte());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeSliceOperation(OpcodeReader reader, KDataInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeArrayGet(OpcodeReader reader, KDataInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeArraySet(OpcodeReader reader, KDataInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeConditional(OpcodeReader reader, KDataInput in) {
        boolean hasElseBranch = in.readBoolean();
        out.writeBoolean(hasElseBranch);

        reader.read(this, in);
        reader.read(this, in);
        if (hasElseBranch) reader.read(this, in);

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeForEach(OpcodeReader reader, KDataInput in) {
        out.writeShort(in.readShort());

        reader.read(this, in);
        reader.read(this, in);

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeWhile(OpcodeReader reader, KDataInput in) {
        reader.read(this, in);
        reader.read(this, in);

        return CONTINUE;
    }

    @Override
    public ReadResult opcodePatternCase(OpcodeReader reader, KDataInput in) {
        reader.read(this, in);

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

        reader.read(this, in);

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeRestPattern(OpcodeReader reader, KDataInput in) {
        out.writeShort(in.readShort());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeValuePattern(OpcodeReader reader, KDataInput in) {
        KOpcodes.READER.read(this, in);

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeDefaultPattern(OpcodeReader reader, KDataInput in) {
        out.writeShort(in.readShort());
        KOpcodes.READER.read(this, in);

        return CONTINUE;
    }

    public OpcodeBufferProcessor setOut(KDataOutput out) {
        this.out = out;

        return this;
    }

    public KDataOutput getOut() {
        return out;
    }
}
