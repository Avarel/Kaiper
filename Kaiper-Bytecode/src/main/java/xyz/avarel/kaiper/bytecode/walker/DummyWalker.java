package xyz.avarel.kaiper.bytecode.walker;

import xyz.avarel.kaiper.exceptions.InvalidBytecodeException;

import java.io.DataInput;
import java.io.IOException;
import java.util.List;

public class DummyWalker implements BytecodeWalker {

    @Override
    public boolean opcodeEnd(DataInput input, int depth) throws IOException {
        int endId = input.readShort();

        if (endId != (depth - 1)) {
            throw new InvalidBytecodeException("Unexpected End " + endId);
        }
        return false;
    }

    @Override
    public void opcodeReturn() throws IOException {
    }

    @Override
    public void opcodeUndefinedConstant() throws IOException {
    }

    @Override
    public void opcodeBooleanConstantTrue() throws IOException {
    }

    @Override
    public void opcodeBooleanConstantFalse() throws IOException {
    }

    @Override
    public void opcodeIntConstant(DataInput input) throws IOException {
        input.readInt();
    }

    @Override
    public void opcodeDecimalConstant(DataInput input) throws IOException {
        input.readDouble();
    }

    @Override
    public void opcodeStringConstant(DataInput input, List<String> stringPool) throws IOException {
        input.readShort();
    }

    @Override
    public void opcodeNewArray() throws IOException {
    }

    @Override
    public void opcodeNewDictionary() throws IOException {
    }

    @Override
    public void opcodeNewRange() throws IOException {
    }

    @Override
    public void opcodeNewFunction(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        input.readShort();
        reader.walkInsts(input, this, stringPool, depth + 1);
        reader.walkInsts(input, this, stringPool, depth + 1);
    }

    @Override
    public void opcodeDefineFunctionParam(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        int modifiers = input.readByte();
        input.readShort();

        if ((modifiers & 1) == 1) {
            reader.walkInsts(input, this, stringPool, depth + 1);
        }
    }


    @Override
    public void opcodeNewModule(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        input.readShort();

        reader.walkInsts(input, this, stringPool, depth + 1);
    }

    @Override
    public void opcodeNewType(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        input.readShort();

        reader.walkInsts(input, this, stringPool, depth + 1);
        reader.walkInsts(input, this, stringPool, depth + 1);
        reader.walkInsts(input, this, stringPool, depth + 1);
        reader.walkInsts(input, this, stringPool, depth + 1);
    }

    @Override
    public void opcodeInvoke(DataInput input) throws IOException {
        input.readByte();
    }

    @Override
    public void opcodeDeclare(DataInput input, List<String> stringPool) throws IOException {
        input.readShort();
    }

    @Override
    public void opcodeUnaryOperation(DataInput input) throws IOException {
        input.readByte();
    }

    @Override
    public void opcodeBinaryOperation(DataInput input) throws IOException {
        input.readByte();
    }

    @Override
    public void opcodeSliceOperation() throws IOException {
    }

    @Override
    public void opcodeGet() throws IOException {
    }

    @Override
    public void opcodeSet() throws IOException {
    }

    @Override
    public void opcodeIdentifier(DataInput input, List<String> stringPool) throws IOException {
        input.readBoolean();
        input.readShort();
    }

    @Override
    public void opcodeAssign(DataInput input, List<String> stringPool) throws IOException {
        input.readBoolean();
        input.readShort();
    }

    @Override
    public void opcodeConditional(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        boolean hasElseBranch = input.readBoolean();
        reader.walkInsts(input, this, stringPool, depth + 1);
        reader.walkInsts(input, this, stringPool, depth + 1);
        if (hasElseBranch) reader.walkInsts(input, this, stringPool, depth + 1);
    }

    @Override
    public void opcodeForEach(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        input.readShort();
        reader.walkInsts(input, this, stringPool, depth + 1);
        reader.walkInsts(input, this, stringPool, depth + 1);
    }

    @Override
    public void opcodeDup() throws IOException {
    }

    @Override
    public void opcodePop() throws IOException {
    }
}
