package xyz.avarel.kaiper.bytecode;

import xyz.avarel.kaiper.bytecode.io.ByteOutput;

public interface Opcode {
    int code();

    String name();

    default void writeInto(ByteOutput out) {
        out.writeByte(code());
    }
}
