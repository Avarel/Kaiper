package xyz.avarel.kaiper.bytecode.reader;

import xyz.avarel.kaiper.bytecode.io.KDataInput;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.reader.consumers.ReadResult;

public interface OpcodeProcessor {
    ReadResult process(OpcodeReader reader, Opcode opcode, KDataInput in);
}
