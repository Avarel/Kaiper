package xyz.avarel.kaiper.bytecode;

import xyz.avarel.kaiper.bytecode.io.ByteOutput;

import java.io.DataOutput;
import java.io.IOException;

public interface Opcode extends DataOutputConsumer {
    int code();

    String name();

    @Override
    default void writeInto(DataOutput out) throws IOException {
        out.writeByte(code());
    }

    default void writeInto(ByteOutput out) {
        out.writeByte(code());
    }
}
