package xyz.avarel.kaiper.bytecode.reader.processors;

import xyz.avarel.kaiper.bytecode.io.KDataInput;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;

public interface KOpcodeProcessor {

    ReadResult opcodeEnd(OpcodeReader reader, KDataInput in);

    ReadResult opcodeLineNumber(OpcodeReader reader, KDataInput in);

    ReadResult opcodeBreakpoint(OpcodeReader reader, KDataInput in);

    ReadResult opcodeReturn(OpcodeReader reader, KDataInput in);

    ReadResult opcodeDup(OpcodeReader reader, KDataInput in);

    ReadResult opcodePop(OpcodeReader reader, KDataInput in);

    ReadResult opcodeNullConstant(OpcodeReader reader, KDataInput in);

    ReadResult opcodeBooleanConstant(OpcodeReader reader, boolean value, KDataInput in);

    ReadResult opcodeIntConstant(OpcodeReader reader, KDataInput in);

    ReadResult opcodeDecimalConstant(OpcodeReader reader, KDataInput in);

    ReadResult opcodeStringConstant(OpcodeReader reader, KDataInput in);

    ReadResult opcodeNewArray(OpcodeReader reader, KDataInput in);

    ReadResult opcodeNewDictionary(OpcodeReader reader, KDataInput in);

    ReadResult opcodeNewRange(OpcodeReader reader, KDataInput in);

    ReadResult opcodeNewFunction(OpcodeReader reader, KDataInput in);

    ReadResult opcodeNewModule(OpcodeReader reader, KDataInput in);

    ReadResult opcodeNewType(OpcodeReader reader, KDataInput in);

    ReadResult opcodeNewTuple(OpcodeReader reader, KDataInput in);

    ReadResult opcodeDeclare(OpcodeReader reader, KDataInput in);

    ReadResult opcodeAssign(OpcodeReader reader, KDataInput in);

    ReadResult opcodeIdentifier(OpcodeReader reader, KDataInput in);

    ReadResult opcodeBindDeclare(OpcodeReader reader, KDataInput in);

    ReadResult opcodeBindAssign(OpcodeReader reader, KDataInput in);

    ReadResult opcodeInvoke(OpcodeReader reader, KDataInput in);

    ReadResult opcodeUnaryOperation(OpcodeReader reader, KDataInput in);

    ReadResult opcodeBinaryOperation(OpcodeReader reader, KDataInput in);

    ReadResult opcodeSliceOperation(OpcodeReader reader, KDataInput in);

    ReadResult opcodeArrayGet(OpcodeReader reader, KDataInput in);

    ReadResult opcodeArraySet(OpcodeReader reader, KDataInput in);

    ReadResult opcodeConditional(OpcodeReader reader, KDataInput in);

    ReadResult opcodeForEach(OpcodeReader reader, KDataInput in);

    ReadResult opcodeWhile(OpcodeReader reader, KDataInput in);

    ReadResult unknownOpcode(OpcodeReader reader, Opcode opcode, KDataInput in);
}
