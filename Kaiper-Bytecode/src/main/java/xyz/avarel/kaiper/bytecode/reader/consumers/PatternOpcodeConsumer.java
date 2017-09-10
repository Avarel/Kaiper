package xyz.avarel.kaiper.bytecode.reader.consumers;

import xyz.avarel.kaiper.bytecode.io.ByteInput;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;

public interface PatternOpcodeConsumer {

    boolean opcodeEnd(OpcodeReader reader, ByteInput in);

    boolean opcodePatternCase(OpcodeReader reader, ByteInput in);

    boolean opcodeWildcardPattern(OpcodeReader reader, ByteInput in);

    boolean opcodeVariablePattern(OpcodeReader reader, ByteInput in);

    boolean opcodeTuplePattern(OpcodeReader reader, ByteInput in);

    boolean opcodeRestPattern(OpcodeReader reader, ByteInput in);

    boolean opcodeValuePattern(OpcodeReader reader, ByteInput in);

    boolean opcodeDefaultPattern(OpcodeReader reader, ByteInput in);

    boolean unknownOpcode(OpcodeReader reader, Opcode opcode, ByteInput in);
}
