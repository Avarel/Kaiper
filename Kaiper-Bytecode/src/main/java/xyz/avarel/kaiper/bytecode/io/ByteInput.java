package xyz.avarel.kaiper.bytecode.io;

public interface ByteInput {
    void readFully(byte b[]);

    void readFully(byte b[], int off, int len);

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
