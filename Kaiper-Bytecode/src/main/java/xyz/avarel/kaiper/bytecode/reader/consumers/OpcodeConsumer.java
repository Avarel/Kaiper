package xyz.avarel.kaiper.bytecode.reader.consumers;

import xyz.avarel.kaiper.bytecode.io.ByteInput;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;

public interface OpcodeConsumer {

    boolean opcodeEnd(OpcodeReader reader, ByteInput in);

    boolean opcodeLineNumber(OpcodeReader reader, ByteInput in);

    boolean opcodeReturn(OpcodeReader reader, ByteInput in);

    boolean opcodeDup(OpcodeReader reader, ByteInput in);

    boolean opcodePop(OpcodeReader reader, ByteInput in);

    boolean opcodeNullConstant(OpcodeReader reader, ByteInput in);

    boolean opcodeBooleanConstant(OpcodeReader reader, boolean value, ByteInput in);

    boolean opcodeIntConstant(OpcodeReader reader, ByteInput in);

    boolean opcodeDecimalConstant(OpcodeReader reader, ByteInput in);

    boolean opcodeStringConstant(OpcodeReader reader, ByteInput in);

    boolean opcodeNewArray(OpcodeReader reader, ByteInput in);

    boolean opcodeNewDictionary(OpcodeReader reader, ByteInput in);

    boolean opcodeNewRange(OpcodeReader reader, ByteInput in);

    boolean opcodeNewFunction(OpcodeReader reader, ByteInput in);

    boolean opcodeNewModule(OpcodeReader reader, ByteInput in);

    boolean opcodeNewType(OpcodeReader reader, ByteInput in);

    boolean opcodeNewTuple(OpcodeReader reader, ByteInput in);

    boolean opcodeDeclare(OpcodeReader reader, ByteInput in);

    boolean opcodeAssign(OpcodeReader reader, ByteInput in);

    boolean opcodeIdentifier(OpcodeReader reader, ByteInput in);

    boolean opcodeBindDeclare(OpcodeReader reader, ByteInput in);

    boolean opcodeBindAssign(OpcodeReader reader, ByteInput in);

    boolean opcodeInvoke(OpcodeReader reader, ByteInput in);

    boolean opcodeUnaryOperation(OpcodeReader reader, ByteInput in);

    boolean opcodeBinaryOperation(OpcodeReader reader, ByteInput in);

    boolean opcodeSliceOperation(OpcodeReader reader, ByteInput in);

    boolean opcodeArrayGet(OpcodeReader reader, ByteInput in);

    boolean opcodeArraySet(OpcodeReader reader, ByteInput in);

    boolean opcodeConditional(OpcodeReader reader, ByteInput in);

    boolean opcodeForEach(OpcodeReader reader, ByteInput in);

    boolean opcodeWhile(OpcodeReader reader, ByteInput in);

    boolean unknownOpcode(OpcodeReader reader, Opcode opcode, ByteInput in);
}
