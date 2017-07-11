package xyz.avarel.aje.bytecode;

import xyz.avarel.aje.exceptions.InvalidBytecodeException;

public enum Bytecode {
    //Special Instruction (Always need to be 0)
    END,

    //Expressions
    RETURN, ASSIGN, CONDITIONAL, FOREACH,

    //Nodes
    ARRAY, DICTIONARY, RANGE,

    //Value Nodes
    UNDEFINED, BOOLEAN, INT, DECIMAL, STRING,

    //Function Nodes (Function Param is a Reserved instruction for Data)
    FUNCTION, FUNCTION_PARAM,

    //Identifier and Invocation
    IDENTIFIER, INVOCATION,

    //Operations
    UNARY_OP, BINARY_OP, SLICE_OP, GET, SET;

    public static Bytecode byID(byte id) {
        Bytecode[] values = values();
        if (values.length > id) return values[id];
        throw new InvalidBytecodeException("Invalid Instruction");
    }

    public byte id() {
        return (byte) ordinal();
    }
}
