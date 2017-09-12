package xyz.avarel.kaiper.bytecode.reader.processors;

import xyz.avarel.kaiper.bytecode.io.KDataInput;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.opcodes.PatternOpcodes;
import xyz.avarel.kaiper.bytecode.reader.OpcodeProcessor;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.exceptions.InvalidBytecodeException;

public abstract class PatternOpcodeProcessorAdapter implements OpcodeProcessor, PatternOpcodeProcessor {
    @Override
    public ReadResult process(OpcodeReader reader, Opcode opcode, KDataInput in) {
        if (opcode instanceof PatternOpcodes) {
            switch ((PatternOpcodes) opcode) {
                case END:
                    return opcodeEnd(reader, in);
                case BREAKPOINT:
                    return opcodeBreakpoint(reader, in);
                case PATTERN_CASE:
                    return opcodePatternCase(reader, in);
                case WILDCARD:
                    return opcodeWildcardPattern(reader, in);
                case VARIABLE:
                    return opcodeVariablePattern(reader, in);
                case TUPLE:
                    return opcodeTuplePattern(reader, in);
                case REST:
                    return opcodeRestPattern(reader, in);
                case VALUE:
                    return opcodeValuePattern(reader, in);
                case DEFAULT:
                    return opcodeDefaultPattern(reader, in);
            }
        }

        return unknownOpcode(reader, opcode, in);
    }

    @Override
    public ReadResult unknownOpcode(OpcodeReader reader, Opcode opcode, KDataInput in) {
        throw new InvalidBytecodeException(opcode);
    }
}
