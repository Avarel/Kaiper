package xyz.avarel.kaiper.bytecode;

import java.io.DataOutput;
import java.io.IOException;

public interface Opcode extends DataOutputConsumer {
    int code();

    String name();

    @Override
    default void writeInto(DataOutput out) throws IOException {
        out.writeByte(code());
    }
}
