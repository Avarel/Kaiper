package xyz.avarel.kaiper.bytecode.reader.consumers;

import xyz.avarel.kaiper.bytecode.io.ByteInput;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;

public interface PatternOpcodeConsumer {

    ReadResult opcodeEnd(OpcodeReader reader, ByteInput in);

    ReadResult opcodePatternCase(OpcodeReader reader, ByteInput in);

    ReadResult opcodeWildcardPattern(OpcodeReader reader, ByteInput in);

    ReadResult opcodeVariablePattern(OpcodeReader reader, ByteInput in);

    ReadResult opcodeTuplePattern(OpcodeReader reader, ByteInput in);

    ReadResult opcodeRestPattern(OpcodeReader reader, ByteInput in);

    ReadResult opcodeValuePattern(OpcodeReader reader, ByteInput in);

    ReadResult opcodeDefaultPattern(OpcodeReader reader, ByteInput in);

    ReadResult unknownOpcode(OpcodeReader reader, Opcode opcode, ByteInput in);
}
