package xyz.avarel.kaiper.bytecode.io;

import java.io.DataInput;
import java.io.IOException;
import java.io.UncheckedIOException;

public class ByteDataInput implements DataInput, ByteInput {
    private final DataInput wrapped;

    public ByteDataInput(DataInput dataInput) {
        this.wrapped = dataInput;
    }

    @Override
    public void readFully(byte[] b) {
        try {
            wrapped.readFully(b);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void readFully(byte[] b, int off, int len) {
        try {
            wrapped.readFully(b, off, len);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public int skipBytes(int n) {
        try {
            return wrapped.skipBytes(n);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean readBoolean() {
        try {
            return wrapped.readBoolean();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public byte readByte() {
        try {
            return wrapped.readByte();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public int readUnsignedByte() {
        try {
            return wrapped.readUnsignedByte();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public short readShort() {
        try {
            return wrapped.readShort();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public int readUnsignedShort() {
        try {
            return wrapped.readUnsignedShort();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public char readChar() {
        try {
            return wrapped.readChar();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public int readInt() {
        try {
            return wrapped.readInt();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public long readLong() {
        try {
            return wrapped.readLong();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public float readFloat() {
        try {
            return wrapped.readFloat();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public double readDouble() {
        try {
            return wrapped.readDouble();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String readString() {
        return readUTF();
    }

    @Override
    public String readLine() {
        try {
            return wrapped.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String readUTF() {
        try {
            return wrapped.readUTF();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
