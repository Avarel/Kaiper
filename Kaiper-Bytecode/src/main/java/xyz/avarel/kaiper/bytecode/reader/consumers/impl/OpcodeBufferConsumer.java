package xyz.avarel.kaiper.bytecode.reader.consumers.impl;

import xyz.avarel.kaiper.bytecode.io.ByteInput;
import xyz.avarel.kaiper.bytecode.io.ByteOutput;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.bytecode.reader.consumers.OpcodeConsumerAdapter;
import xyz.avarel.kaiper.exceptions.InvalidBytecodeException;

public class OpcodeBufferConsumer extends OpcodeConsumerAdapter {
    private final ByteOutput out;
    private int depth = 0;

    public OpcodeBufferConsumer(ByteOutput out) {
        this.out = out;
    }

    public OpcodeBufferConsumer(ByteOutput out, int depth) {
        this.out = out;
        this.depth = depth;
    }

    @Override
    public boolean accept(OpcodeReader reader, Opcode opcode, ByteInput in) {
        out.writeOpcode(opcode);
        return super.accept(reader, opcode, in);
    }

    @Override
    public boolean opcodeEnd(OpcodeReader reader, ByteInput in) {
        short endId = in.readShort();
        out.writeShort(endId);

        if (endId != (depth - 1)) {
            throw new InvalidBytecodeException("Unexpected End " + endId);
        }

        return false;
    }

    @Override
    public boolean opcodeLineNumber(OpcodeReader reader, ByteInput in) {
        return false;
    }

    @Override
    public boolean opcodeReturn(OpcodeReader reader, ByteInput in) {
        return true;
    }

    @Override
    public boolean opcodeDup(OpcodeReader reader, ByteInput in) {
        return false;
    }

    @Override
    public boolean opcodePop(OpcodeReader reader, ByteInput in) {
        return false;
    }

    @Override
    public boolean opcodeNullConstant(OpcodeReader reader, ByteInput in) {
        return true;
    }

    @Override
    public boolean opcodeBooleanConstant(OpcodeReader reader, boolean value, ByteInput in) {
        return false;
    }

    @Override
    public boolean opcodeIntConstant(OpcodeReader reader, ByteInput in) {
        out.writeInt(in.readInt());
        return true;
    }

    @Override
    public boolean opcodeDecimalConstant(OpcodeReader reader, ByteInput in) {
        out.writeDouble(in.readDouble());
        return true;
    }

    @Override
    public boolean opcodeStringConstant(OpcodeReader reader, ByteInput in) {
        out.writeShort(in.readShort());
        return true;
    }

    @Override
    public boolean opcodeNewArray(OpcodeReader reader, ByteInput in) {
        out.writeInt(in.readInt());
        return true;
    }

    @Override
    public boolean opcodeNewDictionary(OpcodeReader reader, ByteInput in) {
        return true;
    }

    @Override
    public boolean opcodeNewRange(OpcodeReader reader, ByteInput in) {
        return true;
    }

    @Override
    public boolean opcodeNewFunction(OpcodeReader reader, ByteInput in) {
        return true; // TODO: 10/09/2017
    }

    @Override
    public boolean opcodeNewModule(OpcodeReader reader, ByteInput in) {
        return true; // TODO: 10/09/2017
    }

    @Override
    public boolean opcodeNewType(OpcodeReader reader, ByteInput in) {
        return true; // TODO: 10/09/2017
    }

    @Override
    public boolean opcodeNewTuple(OpcodeReader reader, ByteInput in) {
        return true; // TODO: 10/09/2017
    }

    @Override
    public boolean opcodeDeclare(OpcodeReader reader, ByteInput in) {
        out.writeShort(in.readShort());
        return true;
    }

    @Override
    public boolean opcodeAssign(OpcodeReader reader, ByteInput in) {
        out.writeBoolean(in.readBoolean()).writeShort(in.readShort());
        return true;
    }

    @Override
    public boolean opcodeIdentifier(OpcodeReader reader, ByteInput in) {
        out.writeBoolean(in.readBoolean()).writeShort(in.readShort());
        return true;
    }

    @Override
    public boolean opcodeBindDeclare(OpcodeReader reader, ByteInput in) {
        return false;
    }

    @Override
    public boolean opcodeBindAssign(OpcodeReader reader, ByteInput in) {
        return false;
    }

    @Override
    public boolean opcodeInvoke(OpcodeReader reader, ByteInput in) {
        return true;
    }

    @Override
    public boolean opcodeUnaryOperation(OpcodeReader reader, ByteInput in) {
        out.writeByte(in.readByte());
        return false;
    }

    @Override
    public boolean opcodeBinaryOperation(OpcodeReader reader, ByteInput in) {
        out.writeByte(in.readByte());
        return false;
    }

    @Override
    public boolean opcodeSliceOperation(OpcodeReader reader, ByteInput in) {
        return false;
    }

    @Override
    public boolean opcodeArrayGet(OpcodeReader reader, ByteInput in) {
        return false;
    }

    @Override
    public boolean opcodeArraySet(OpcodeReader reader, ByteInput in) {
        return false;
    }

    @Override
    public boolean opcodeConditional(OpcodeReader reader, ByteInput in) {
        boolean hasElseBranch = in.readBoolean();
        out.writeBoolean(hasElseBranch);

        depth++;
        reader.read(this, in);
        reader.read(this, in);
        if (hasElseBranch) reader.read(this, in);
        depth--;

        return false;
    }

    @Override
    public boolean opcodeForEach(OpcodeReader reader, ByteInput in) {
        out.writeShort(in.readShort());

        depth++;
        reader.read(this, in);
        reader.read(this, in);
        depth--;

        return false;
    }

    @Override
    public boolean opcodeWhile(OpcodeReader reader, ByteInput in) {
        depth++;
        reader.read(this, in);
        reader.read(this, in);
        depth--;

        return false;
    }
}
