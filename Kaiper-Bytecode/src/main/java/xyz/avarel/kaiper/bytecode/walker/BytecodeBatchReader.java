package xyz.avarel.kaiper.bytecode.walker;

import xyz.avarel.kaiper.bytecode.BytecodeUtils;
import xyz.avarel.kaiper.bytecode.Opcode;
import xyz.avarel.kaiper.bytecode.Opcodes;

import java.io.DataInput;
import java.io.IOException;
import java.util.List;

import static xyz.avarel.kaiper.bytecode.Opcodes.*;

public class BytecodeBatchReader {

    public void walkInsts(DataInput input, BytecodeWalker walker, List<String> stringPool, int depth) throws IOException {
        while (nextInst(input, walker, stringPool, depth)) ;
    }

    public boolean nextInst(DataInput input, BytecodeWalker walker, List<String> stringPool, int depth) throws IOException {
        byte opcode = input.readByte();
        Opcode op;
        switch (op = Opcodes.byId(opcode)) {
            case END:
                return walker.opcodeEnd(input, depth);
            case RETURN:
                return walker.opcodeReturn();
            case U_CONST:
                walker.opcodeUndefinedConstant();
                break;
            case B_CONST_TRUE:
                walker.opcodeBooleanConstantTrue();
                break;
            case B_CONST_FALSE:
                walker.opcodeBooleanConstantFalse();
                break;
            case I_CONST:
                walker.opcodeIntConstant(input);
                break;
            case D_CONST:
                walker.opcodeDecimalConstant(input);
                break;
            case S_CONST:
                walker.opcodeStringConstant(input, stringPool);
                break;
            case NEW_ARRAY:
                walker.opcodeNewArray();
                break;
            case NEW_DICTIONARY:
                walker.opcodeNewDictionary();
                break;
            case NEW_RANGE:
                walker.opcodeNewRange();
                break;
            case NEW_FUNCTION:
                walker.opcodeNewFunction(input, this, stringPool, depth);
                break;
            case NEW_MODULE:
                walker.opcodeNewModule(input, this, stringPool, depth);
                break;
            case NEW_TYPE:
                walker.opcodeNewType(input, this, stringPool, depth);
                break;
            case PARAM_CASE:
                walker.opcodeDefineFunctionParam(input, this, stringPool, depth);
                break;
            case INVOKE:
                walker.opcodeInvoke(input);
                break;
            case DECLARE:
                walker.opcodeDeclare(input, stringPool);
                break;
            case UNARY_OPERATION:
                walker.opcodeUnaryOperation(input);
                break;
            case BINARY_OPERATION:
                walker.opcodeBinaryOperation(input);
                break;
            case SLICE_OPERATION:
                walker.opcodeSliceOperation();
                break;
            case ARRAY_GET:
                walker.opcodeGet();
                break;
            case ARRAY_SET:
                walker.opcodeSet();
                break;
            case IDENTIFIER:
                walker.opcodeIdentifier(input, stringPool);
                break;
            case ASSIGN:
                walker.opcodeAssign(input, stringPool);
                break;
            case CONDITIONAL:
                walker.opcodeConditional(input, this, stringPool, depth);
                break;
            case FOR_EACH:
                walker.opcodeForEach(input, this, stringPool, depth);
                break;
            case DUP:
                walker.opcodeDup();
                break;
            case POP:
                walker.opcodePop();
                break;
            default:
                walker.opcode(op, input, this, stringPool, depth);
        }

        return true;
    }
}
