package xyz.avarel.kaiper.bytecode.reader;

import xyz.avarel.kaiper.bytecode.io.ByteInput;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.reader.consumers.ReadResult;

public interface BaseOpcodeConsumer {
    ReadResult accept(OpcodeReader reader, Opcode opcode, ByteInput in);
}
