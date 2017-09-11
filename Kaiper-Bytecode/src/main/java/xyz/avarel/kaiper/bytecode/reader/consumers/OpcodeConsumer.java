package xyz.avarel.kaiper.bytecode.reader.consumers;

import xyz.avarel.kaiper.bytecode.io.ByteInput;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;

public interface OpcodeConsumer {

    ReadResult opcodeEnd(OpcodeReader reader, ByteInput in);

    ReadResult opcodeLineNumber(OpcodeReader reader, ByteInput in);

    ReadResult opcodeBreakpoint(OpcodeReader reader, ByteInput in);

    ReadResult opcodeReturn(OpcodeReader reader, ByteInput in);

    ReadResult opcodeDup(OpcodeReader reader, ByteInput in);

    ReadResult opcodePop(OpcodeReader reader, ByteInput in);

    ReadResult opcodeNullConstant(OpcodeReader reader, ByteInput in);

    ReadResult opcodeBooleanConstant(OpcodeReader reader, boolean value, ByteInput in);

    ReadResult opcodeIntConstant(OpcodeReader reader, ByteInput in);

    ReadResult opcodeDecimalConstant(OpcodeReader reader, ByteInput in);

    ReadResult opcodeStringConstant(OpcodeReader reader, ByteInput in);

    ReadResult opcodeNewArray(OpcodeReader reader, ByteInput in);

    ReadResult opcodeNewDictionary(OpcodeReader reader, ByteInput in);

    ReadResult opcodeNewRange(OpcodeReader reader, ByteInput in);

    ReadResult opcodeNewFunction(OpcodeReader reader, ByteInput in);

    ReadResult opcodeNewModule(OpcodeReader reader, ByteInput in);

    ReadResult opcodeNewType(OpcodeReader reader, ByteInput in);

    ReadResult opcodeNewTuple(OpcodeReader reader, ByteInput in);

    ReadResult opcodeDeclare(OpcodeReader reader, ByteInput in);

    ReadResult opcodeAssign(OpcodeReader reader, ByteInput in);

    ReadResult opcodeIdentifier(OpcodeReader reader, ByteInput in);

    ReadResult opcodeBindDeclare(OpcodeReader reader, ByteInput in);

    ReadResult opcodeBindAssign(OpcodeReader reader, ByteInput in);

    ReadResult opcodeInvoke(OpcodeReader reader, ByteInput in);

    ReadResult opcodeUnaryOperation(OpcodeReader reader, ByteInput in);

    ReadResult opcodeBinaryOperation(OpcodeReader reader, ByteInput in);

    ReadResult opcodeSliceOperation(OpcodeReader reader, ByteInput in);

    ReadResult opcodeArrayGet(OpcodeReader reader, ByteInput in);

    ReadResult opcodeArraySet(OpcodeReader reader, ByteInput in);

    ReadResult opcodeConditional(OpcodeReader reader, ByteInput in);

    ReadResult opcodeForEach(OpcodeReader reader, ByteInput in);

    ReadResult opcodeWhile(OpcodeReader reader, ByteInput in);

    ReadResult unknownOpcode(OpcodeReader reader, Opcode opcode, ByteInput in);
}
