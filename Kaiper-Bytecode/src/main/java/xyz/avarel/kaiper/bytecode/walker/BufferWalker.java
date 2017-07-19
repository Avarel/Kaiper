package xyz.avarel.kaiper.bytecode.walker;

import xyz.avarel.kaiper.exceptions.InvalidBytecodeException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import static xyz.avarel.kaiper.bytecode.Opcodes.*;

public class BufferWalker implements BytecodeWalker {
    private final DataOutput out;

    public BufferWalker(DataOutput out) {
        this.out = out;
    }

    @Override
    public boolean opcodeEnd(DataInput input, int depth) throws IOException {
        short endId = input.readShort();

        END.writeInto(out);
        out.writeShort(endId);

        if (endId != (depth - 1)) {
            throw new InvalidBytecodeException("Unexpected End " + endId);
        }

        return false;
    }

    @Override
    public void opcodeReturn() throws IOException {
        RETURN.writeInto(out);
    }

    @Override
    public void opcodeUndefinedConstant() throws IOException {
        U_CONST.writeInto(out);
    }

    @Override
    public void opcodeBooleanConstantTrue() throws IOException {
        B_CONST_TRUE.writeInto(out);
    }

    @Override
    public void opcodeBooleanConstantFalse() throws IOException {
        B_CONST_FALSE.writeInto(out);
    }

    @Override
    public void opcodeIntConstant(DataInput input) throws IOException {
        I_CONST.writeInto(out);
        out.writeInt(input.readInt());
    }

    @Override
    public void opcodeDecimalConstant(DataInput input) throws IOException {
        D_CONST.writeInto(out);
        out.writeDouble(input.readDouble());
    }

    @Override
    public void opcodeStringConstant(DataInput input, List<String> stringPool) throws IOException {
        S_CONST.writeInto(out);
        out.writeShort(input.readShort());
    }

    @Override
    public void opcodeNewArray() throws IOException {
        NEW_ARRAY.writeInto(out);
    }

    @Override
    public void opcodeNewDictionary() throws IOException {
        NEW_DICTIONARY.writeInto(out);
    }

    @Override
    public void opcodeNewRange() throws IOException {
        NEW_RANGE.writeInto(out);
    }

    @Override
    public void opcodeNewFunction(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        NEW_FUNCTION.writeInto(out);
        out.writeShort(input.readShort());

        reader.walkInsts(input, this, stringPool, depth + 1);
        reader.walkInsts(input, this, stringPool, depth + 1);
    }

    @Override
    public void opcodeDefineFunctionParam(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        FUNCTION_DEF_PARAM.writeInto(out);
        int modifiers;
        out.writeByte(modifiers = input.readByte());
        out.writeShort(input.readShort());

        if ((modifiers & 1) == 1) {
            reader.walkInsts(input, this, stringPool, depth + 1);
        }
    }

    @Override
    public void opcodeNewModule(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        NEW_MODULE.writeInto(out);
        out.writeShort(input.readShort());

        reader.walkInsts(input, this, stringPool, depth + 1);
    }

    @Override
    public void opcodeNewType(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        NEW_TYPE.writeInto(out);
        out.writeShort(input.readShort());

        reader.walkInsts(input, this, stringPool, depth + 1);
        reader.walkInsts(input, this, stringPool, depth + 1);
        reader.walkInsts(input, this, stringPool, depth + 1);
        reader.walkInsts(input, this, stringPool, depth + 1);
    }

    @Override
    public void opcodeInvoke(DataInput input) throws IOException {
        INVOKE.writeInto(out);
        out.writeByte(input.readByte());
    }

    @Override
    public void opcodeDeclare(DataInput input, List<String> stringPool) throws IOException {
        DECLARE.writeInto(out);
        out.writeShort(input.readShort());
    }

    @Override
    public void opcodeUnaryOperation(DataInput input) throws IOException {
        UNARY_OPERATION.writeInto(out);
        out.writeByte(input.readByte());
    }

    @Override
    public void opcodeBinaryOperation(DataInput input) throws IOException {
        BINARY_OPERATION.writeInto(out);
        out.writeByte(input.readByte());
    }

    @Override
    public void opcodeSliceOperation() throws IOException {
        SLICE_OPERATION.writeInto(out);
    }

    @Override
    public void opcodeGet() throws IOException {
        GET.writeInto(out);
    }

    @Override
    public void opcodeSet() throws IOException {
        SET.writeInto(out);
    }

    @Override
    public void opcodeIdentifier(DataInput input, List<String> stringPool) throws IOException {
        IDENTIFIER.writeInto(out);
        out.writeBoolean(input.readBoolean());
        out.writeShort(input.readShort());
    }

    @Override
    public void opcodeAssign(DataInput input, List<String> stringPool) throws IOException {
        ASSIGN.writeInto(out);
        out.writeBoolean(input.readBoolean());
        out.writeShort(input.readShort());
    }

    @Override
    public void opcodeConditional(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        boolean hasElseBranch = input.readBoolean();

        CONDITIONAL.writeInto(out);
        out.writeBoolean(hasElseBranch);

        reader.walkInsts(input, this, stringPool, depth + 1);
        reader.walkInsts(input, this, stringPool, depth + 1);
        if (hasElseBranch) reader.walkInsts(input, this, stringPool, depth + 1);
    }

    @Override
    public void opcodeForEach(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        FOR_EACH.writeInto(out);
        out.writeShort(input.readShort());

        reader.walkInsts(input, this, stringPool, depth + 1);
        reader.walkInsts(input, this, stringPool, depth + 1);
    }

    @Override
    public void opcodeDup() throws IOException {
        DUP.writeInto(out);
    }

    @Override
    public void opcodePop() throws IOException {
        POP.writeInto(out);
    }
}
