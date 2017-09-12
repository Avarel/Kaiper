package xyz.avarel.kaiper.bytecode.io;

import xyz.avarel.kaiper.bytecode.opcodes.Opcode;

import java.io.OutputStream;

public interface KDataOutput {
    KDataOutput writeByte(int b);

    KDataOutput writeBytes(byte b[]);

    KDataOutput writeBytes(byte b[], int off, int len);

    KDataOutput writeBoolean(boolean v);

    KDataOutput writeShort(int v);

    KDataOutput writeChar(int v);

    KDataOutput writeInt(int v);

    KDataOutput writeLong(long v);

    KDataOutput writeFloat(float v);

    KDataOutput writeDouble(double v);

    KDataOutput writeBytes(String s);

    KDataOutput writeChars(String s);

    KDataOutput writeString(String s);

    default KDataOutput writeOpcode(Opcode opcode) {
        return writeByte(opcode.code());
    }

    OutputStream getOutputStream();
}
