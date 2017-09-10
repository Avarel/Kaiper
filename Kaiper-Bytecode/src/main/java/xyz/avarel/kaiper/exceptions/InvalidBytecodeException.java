package xyz.avarel.kaiper.exceptions;

import xyz.avarel.kaiper.bytecode.opcodes.Opcode;

import static xyz.avarel.kaiper.bytecode.BytecodeUtils.toHex;

public class InvalidBytecodeException extends KaiperException {
    public InvalidBytecodeException(byte... bytes) {
        super("Invalid byte sequence 0x" + toHex(bytes));
    }

    public InvalidBytecodeException(Opcode opcode) {
        super("Invalid instruction " + opcode.name() + " (0x" + toHex((byte) opcode.code()) + ")");
    }

    public InvalidBytecodeException(Opcode opcode, byte... bytes) {
        super("Invalid instruction " + opcode.name() + " and byte sequence (0x" + toHex((byte) opcode.code()) + toHex(bytes) + ")");
    }

    public InvalidBytecodeException(String msg) {
        super(msg);
    }
}
