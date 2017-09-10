package xyz.avarel.kaiper.bytecode.reader;

import xyz.avarel.kaiper.bytecode.io.ByteInput;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;

public interface BaseOpcodeConsumer {
    boolean accept(OpcodeReader reader, Opcode opcode, ByteInput in);
}
