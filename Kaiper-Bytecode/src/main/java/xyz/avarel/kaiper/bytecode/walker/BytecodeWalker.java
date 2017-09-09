package xyz.avarel.kaiper.bytecode.walker;

import xyz.avarel.kaiper.bytecode.Opcode;
import xyz.avarel.kaiper.exceptions.InvalidBytecodeException;

import java.io.DataInput;
import java.io.IOException;
import java.util.List;

public interface BytecodeWalker {
    boolean opcodeEnd(DataInput input, int depth) throws IOException;

    boolean opcodeReturn() throws IOException;

    void opcodeUndefinedConstant() throws IOException;

    void opcodeBooleanConstantTrue() throws IOException;

    void opcodeBooleanConstantFalse() throws IOException;

    void opcodeIntConstant(DataInput input) throws IOException;

    void opcodeDecimalConstant(DataInput input) throws IOException;

    void opcodeStringConstant(DataInput input, List<String> stringPool) throws IOException;

    void opcodeNewArray() throws IOException;

    void opcodeNewDictionary() throws IOException;

    void opcodeNewRange() throws IOException;

    void opcodeNewFunction(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException;

    void opcodeNewModule(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException;

    void opcodeNewType(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException;

    void opcodeInvoke(DataInput input) throws IOException;

    void opcodeDeclare(DataInput input, List<String> stringPool) throws IOException;

    void opcodeUnaryOperation(DataInput input) throws IOException;

    void opcodeBinaryOperation(DataInput input) throws IOException;

    void opcodeSliceOperation() throws IOException;

    void opcodeGet() throws IOException;

    void opcodeSet() throws IOException;

    void opcodeIdentifier(DataInput input, List<String> stringPool) throws IOException;

    void opcodeAssign(DataInput input, List<String> stringPool) throws IOException;

    void opcodeConditional(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException;

    void opcodeForEach(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException;

    void opcodeDup() throws IOException;

    void opcodePop() throws IOException;

    default void opcode(Opcode opcode, DataInput input, BytecodeBatchReader bytecodeBatchReader, List<String> stringPool, int depth) throws IOException {
        throw new InvalidBytecodeException(String.format("Invalid Bytecode %s (0x%x)", opcode.name(), opcode.code()));
    }
}
