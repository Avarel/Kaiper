package xyz.avarel.kaiper.bytecode.reader.consumers;

import xyz.avarel.kaiper.bytecode.io.KDataInput;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;

public interface PatternOpcodeProcessor {

    ReadResult opcodeEnd(OpcodeReader reader, KDataInput in);

    ReadResult opcodePatternCase(OpcodeReader reader, KDataInput in);

    ReadResult opcodeWildcardPattern(OpcodeReader reader, KDataInput in);

    ReadResult opcodeVariablePattern(OpcodeReader reader, KDataInput in);

    ReadResult opcodeTuplePattern(OpcodeReader reader, KDataInput in);

    ReadResult opcodeRestPattern(OpcodeReader reader, KDataInput in);

    ReadResult opcodeValuePattern(OpcodeReader reader, KDataInput in);

    ReadResult opcodeDefaultPattern(OpcodeReader reader, KDataInput in);

    ReadResult unknownOpcode(OpcodeReader reader, Opcode opcode, KDataInput in);
}
