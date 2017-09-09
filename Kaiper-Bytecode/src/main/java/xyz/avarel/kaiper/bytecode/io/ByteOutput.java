package xyz.avarel.kaiper.bytecode.io;

import xyz.avarel.kaiper.bytecode.Opcode;

public interface ByteOutput {
    void write(int b);

    void write(byte b[]);

    void write(byte b[], int off, int len);

    void writeBoolean(boolean v);

    void writeByte(int v);

    void writeShort(int v);

    void writeChar(int v);

    void writeInt(int v);

    void writeLong(long v);

    void writeFloat(float v);

    void writeDouble(double v);

    void writeBytes(String s);

    void writeChars(String s);

    void writeString(String s);

    default void write(Opcode opcode) {
        writeByte(opcode.code());
    }
}
