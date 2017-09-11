package xyz.avarel.kaiper.bytecode.io;

import xyz.avarel.kaiper.bytecode.opcodes.Opcode;

import java.io.OutputStream;

public interface ByteOutput {
    ByteOutput writeByte(int b);

    ByteOutput writeBytes(byte b[]);

    ByteOutput writeBytes(byte b[], int off, int len);

    ByteOutput writeBoolean(boolean v);

    ByteOutput writeShort(int v);

    ByteOutput writeChar(int v);

    ByteOutput writeInt(int v);

    ByteOutput writeLong(long v);

    ByteOutput writeFloat(float v);

    ByteOutput writeDouble(double v);

    ByteOutput writeBytes(String s);

    ByteOutput writeChars(String s);

    ByteOutput writeString(String s);

    default ByteOutput writeOpcode(Opcode opcode) {
        return writeByte(opcode.code());
    }

    OutputStream getOutputStream();
}
