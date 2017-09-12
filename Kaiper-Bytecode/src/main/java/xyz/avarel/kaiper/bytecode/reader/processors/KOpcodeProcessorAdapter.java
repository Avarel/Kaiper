package xyz.avarel.kaiper.bytecode.reader.processors;

import xyz.avarel.kaiper.bytecode.io.KDataInput;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.opcodes.KOpcodes;
import xyz.avarel.kaiper.bytecode.reader.OpcodeProcessor;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.exceptions.InvalidBytecodeException;

public abstract class KOpcodeProcessorAdapter implements OpcodeProcessor, KOpcodeProcessor {
    @Override
    public ReadResult process(OpcodeReader reader, Opcode opcode, KDataInput in) {
        if (opcode instanceof KOpcodes) {
            switch ((KOpcodes) opcode) {
                case END:
                    return opcodeEnd(reader, in);
                case LINE_NUMBER:
                    return opcodeLineNumber(reader, in);

                case BREAKPOINT:
                    return opcodeBreakpoint(reader, in);

                case RETURN:
                    return opcodeReturn(reader, in);

                case DUP:
                    return opcodeDup(reader, in);
                case POP:
                    return opcodePop(reader, in);

                case U_CONST:
                    return opcodeNullConstant(reader, in);
                case B_CONST_TRUE:
                    return opcodeBooleanConstant(reader, true, in);
                case B_CONST_FALSE:
                    return opcodeBooleanConstant(reader, false, in);
                case I_CONST:
                    return opcodeIntConstant(reader, in);
                case D_CONST:
                    return opcodeDecimalConstant(reader, in);
                case S_CONST:
                    return opcodeStringConstant(reader, in);

                case NEW_ARRAY:
                    return opcodeNewArray(reader, in);
                case NEW_DICTIONARY:
                    return opcodeNewDictionary(reader, in);
                case NEW_RANGE:
                    return opcodeNewRange(reader, in);
                case NEW_FUNCTION:
                    return opcodeNewFunction(reader, in);
                case NEW_MODULE:
                    return opcodeNewModule(reader, in);
                case NEW_TYPE:
                    return opcodeNewType(reader, in);
                case NEW_TUPLE:
                    return opcodeNewTuple(reader, in);

                case DECLARE:
                    return opcodeDeclare(reader, in);
                case ASSIGN:
                    return opcodeAssign(reader, in);
                case IDENTIFIER:
                    return opcodeIdentifier(reader, in);
                case BIND_DECLARE:
                    return opcodeBindDeclare(reader, in);
                case BIND_ASSIGN:
                    return opcodeBindAssign(reader, in);

                case INVOKE:
                    return opcodeInvoke(reader, in);
                case UNARY_OPERATION:
                    return opcodeUnaryOperation(reader, in);
                case BINARY_OPERATION:
                    return opcodeBinaryOperation(reader, in);
                case SLICE_OPERATION:
                    return opcodeSliceOperation(reader, in);
                case ARRAY_GET:
                    return opcodeArrayGet(reader, in);
                case ARRAY_SET:
                    return opcodeArraySet(reader, in);

                case CONDITIONAL:
                    return opcodeConditional(reader, in);
                case FOR_EACH:
                    return opcodeForEach(reader, in);
                case WHILE:
                    return opcodeWhile(reader, in);
            }
        }

        return unknownOpcode(reader, opcode, in);
    }

    @Override
    public ReadResult unknownOpcode(OpcodeReader reader, Opcode opcode, KDataInput in) {
        throw new InvalidBytecodeException(opcode);
    }
}
