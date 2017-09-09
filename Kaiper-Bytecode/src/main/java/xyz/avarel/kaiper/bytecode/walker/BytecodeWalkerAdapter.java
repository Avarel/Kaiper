package xyz.avarel.kaiper.bytecode.walker;

import xyz.avarel.kaiper.exceptions.InvalidBytecodeException;

import java.io.DataInput;
import java.io.IOException;
import java.util.List;

public abstract class BytecodeWalkerAdapter implements BytecodeWalker {
    @Override
    public boolean opcodeEnd(DataInput input, int depth) throws IOException {
        int endId = input.readShort();

        if (endId != (depth - 1)) {
            throw new InvalidBytecodeException("Illegal END " + endId + " Instruction");
        }

        return false;
    }

    @Override
    public boolean opcodeReturn() throws IOException {
        throw new InvalidBytecodeException("Illegal RETURN Instruction");
    }

    @Override
    public void opcodeUndefinedConstant() throws IOException {
        throw new InvalidBytecodeException("Illegal U_CONST Instruction");
    }

    @Override
    public void opcodeBooleanConstantTrue() throws IOException {
        throw new InvalidBytecodeException("Illegal B_CONST_TRUE Instruction");
    }

    @Override
    public void opcodeBooleanConstantFalse() throws IOException {
        throw new InvalidBytecodeException("Illegal B_CONST_FALSE Instruction");
    }

    @Override
    public void opcodeIntConstant(DataInput input) throws IOException {
        throw new InvalidBytecodeException("Illegal I_CONST Instruction");
    }

    @Override
    public void opcodeDecimalConstant(DataInput input) throws IOException {
        throw new InvalidBytecodeException("Illegal D_CONST Instruction");
    }

    @Override
    public void opcodeStringConstant(DataInput input, List<String> stringPool) throws IOException {
        throw new InvalidBytecodeException("Illegal S_CONST Instruction");
    }

    @Override
    public void opcodeNewArray() throws IOException {
        throw new InvalidBytecodeException("Illegal NEW_ARRAY Instruction");
    }

    @Override
    public void opcodeNewDictionary() throws IOException {
        throw new InvalidBytecodeException("Illegal NEW_DICTIONARY Instruction");
    }

    @Override
    public void opcodeNewRange() throws IOException {
        throw new InvalidBytecodeException("Illegal NEW_RANGE Instruction");
    }

    @Override
    public void opcodeNewFunction(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        throw new InvalidBytecodeException("Illegal NEW_FUNCTION Instruction");
    }

    @Override
    public void opcodeNewModule(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        throw new InvalidBytecodeException("Illegal NEW_MODULE Instruction");
    }

    @Override
    public void opcodeNewType(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        throw new InvalidBytecodeException("Illegal NEW_TYPE Instruction");
    }

    @Override
    public void opcodeInvoke(DataInput input) throws IOException {
        throw new InvalidBytecodeException("Illegal INVOKE Instruction");
    }

    @Override
    public void opcodeDeclare(DataInput input, List<String> stringPool) throws IOException {
        throw new InvalidBytecodeException("Illegal DECLARE Instruction");
    }

    @Override
    public void opcodeUnaryOperation(DataInput input) throws IOException {
        throw new InvalidBytecodeException("Illegal UNARY_OPERATION Instruction");
    }

    @Override
    public void opcodeBinaryOperation(DataInput input) throws IOException {
        throw new InvalidBytecodeException("Illegal BINARY_OPERATION Instruction");
    }

    @Override
    public void opcodeSliceOperation() throws IOException {
        throw new InvalidBytecodeException("Illegal SLICE_OPERATION Instruction");
    }

    @Override
    public void opcodeGet() throws IOException {
        throw new InvalidBytecodeException("Illegal ARRAY_GET Instruction");
    }

    @Override
    public void opcodeSet() throws IOException {
        throw new InvalidBytecodeException("Illegal ARRAY_SET Instruction");
    }

    @Override
    public void opcodeIdentifier(DataInput input, List<String> stringPool) throws IOException {
        throw new InvalidBytecodeException("Illegal IDENTIFIER Instruction");
    }

    @Override
    public void opcodeAssign(DataInput input, List<String> stringPool) throws IOException {
        throw new InvalidBytecodeException("Illegal ASSIGN Instruction");
    }

    @Override
    public void opcodeConditional(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        throw new InvalidBytecodeException("Illegal CONDITIONAL Instruction");
    }

    @Override
    public void opcodeForEach(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        throw new InvalidBytecodeException("Illegal FOREACH Instruction");
    }

    @Override
    public void opcodeDup() throws IOException {
        throw new InvalidBytecodeException("Illegal DUP Instruction");
    }

    @Override
    public void opcodePop() throws IOException {
        throw new InvalidBytecodeException("Illegal POP Instruction");
    }
}
