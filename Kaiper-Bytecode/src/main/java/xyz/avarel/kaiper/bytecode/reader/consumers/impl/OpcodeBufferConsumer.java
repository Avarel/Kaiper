package xyz.avarel.kaiper.bytecode.reader.consumers.impl;

import xyz.avarel.kaiper.bytecode.io.ByteInput;
import xyz.avarel.kaiper.bytecode.io.ByteOutput;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.bytecode.reader.consumers.OpcodeConsumerAdapter;
import xyz.avarel.kaiper.bytecode.reader.consumers.ReadResult;
import xyz.avarel.kaiper.exceptions.InvalidBytecodeException;

import static xyz.avarel.kaiper.bytecode.reader.consumers.ReadResult.*;

public class OpcodeBufferConsumer extends OpcodeConsumerAdapter {
    private ByteOutput out;
    private int depth = 0;

    public OpcodeBufferConsumer() {
        this(null);
    }

    public OpcodeBufferConsumer(ByteOutput out) {
        this.out = out;
    }

    public OpcodeBufferConsumer(int depth) {
        this(null, depth);
    }

    public OpcodeBufferConsumer(ByteOutput out, int depth) {
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
    public ReadResult opcodeLineNumber(OpcodeReader reader, ByteInput in) {
        out.writeLong(in.readLong());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeBreakpoint(OpcodeReader reader, ByteInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeReturn(OpcodeReader reader, ByteInput in) {
        return RETURNED;
    }

    @Override
    public ReadResult opcodeDup(OpcodeReader reader, ByteInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodePop(OpcodeReader reader, ByteInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNullConstant(OpcodeReader reader, ByteInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeBooleanConstant(OpcodeReader reader, boolean value, ByteInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeIntConstant(OpcodeReader reader, ByteInput in) {
        out.writeInt(in.readInt());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeDecimalConstant(OpcodeReader reader, ByteInput in) {
        out.writeDouble(in.readDouble());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeStringConstant(OpcodeReader reader, ByteInput in) {
        out.writeShort(in.readShort());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewArray(OpcodeReader reader, ByteInput in) {
        out.writeInt(in.readInt());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewDictionary(OpcodeReader reader, ByteInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewRange(OpcodeReader reader, ByteInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewFunction(OpcodeReader reader, ByteInput in) {
        out.writeShort(in.readShort());

        depth++;
        reader.read(new PatternOpcodeBufferConsumer(out, depth), in);
        reader.read(this, in);
        depth--;

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewModule(OpcodeReader reader, ByteInput in) {
        out.writeShort(in.readShort());

        depth++;
        reader.read(this, in);
        depth--;

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewType(OpcodeReader reader, ByteInput in) {
        out.writeShort(in.readShort());

        depth++;
        reader.read(new PatternOpcodeBufferConsumer(out, depth), in);
        reader.read(this, in);
        depth--;

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewTuple(OpcodeReader reader, ByteInput in) {
        int size = in.readInt();
        out.writeInt(size);

        depth++;
        for (int i = 0; i < size; i++) {
            out.writeShort(in.readShort());
            reader.read(this, in);
        }
        depth--;

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeDeclare(OpcodeReader reader, ByteInput in) {
        out.writeShort(in.readShort());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeAssign(OpcodeReader reader, ByteInput in) {
        out.writeBoolean(in.readBoolean()).writeShort(in.readShort());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeIdentifier(OpcodeReader reader, ByteInput in) {
        out.writeBoolean(in.readBoolean()).writeShort(in.readShort());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeBindDeclare(OpcodeReader reader, ByteInput in) {
        depth++;
        reader.read(new PatternOpcodeBufferConsumer(out, depth), in);
        depth--;

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeBindAssign(OpcodeReader reader, ByteInput in) {
        depth++;
        reader.read(new PatternOpcodeBufferConsumer(out, depth), in);
        depth--;

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeInvoke(OpcodeReader reader, ByteInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeUnaryOperation(OpcodeReader reader, ByteInput in) {
        out.writeByte(in.readByte());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeBinaryOperation(OpcodeReader reader, ByteInput in) {
        out.writeByte(in.readByte());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeSliceOperation(OpcodeReader reader, ByteInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeArrayGet(OpcodeReader reader, ByteInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeArraySet(OpcodeReader reader, ByteInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeConditional(OpcodeReader reader, ByteInput in) {
        boolean hasElseBranch = in.readBoolean();
        out.writeBoolean(hasElseBranch);

        depth++;
        reader.read(this, in);
        reader.read(this, in);
        if (hasElseBranch) reader.read(this, in);
        depth--;

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeForEach(OpcodeReader reader, ByteInput in) {
        out.writeShort(in.readShort());

        depth++;
        reader.read(this, in);
        reader.read(this, in);
        depth--;

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeWhile(OpcodeReader reader, ByteInput in) {
        depth++;
        reader.read(this, in);
        reader.read(this, in);
        depth--;

        return CONTINUE;
    }

    public ByteOutput getOut() {
        return out;
    }

    public void setOut(ByteOutput out) {
        this.out = out;
    }
}
