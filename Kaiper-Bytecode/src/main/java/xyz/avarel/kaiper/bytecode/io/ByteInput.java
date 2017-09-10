package xyz.avarel.kaiper.bytecode.io;

public interface ByteInput {
    ByteInput readFully(byte b[]);

    ByteInput readFully(byte b[], int off, int len);

    int skipBytes(int n);

    boolean readBoolean();

    byte readByte();

    int readUnsignedByte();

    short readShort();

    int readUnsignedShort();

    char readChar();

    int readInt();

    long readLong();

    float readFloat();

    double readDouble();

    String readString();
}
