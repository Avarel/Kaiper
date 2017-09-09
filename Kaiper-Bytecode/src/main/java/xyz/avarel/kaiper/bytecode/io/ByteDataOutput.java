package xyz.avarel.kaiper.bytecode.io;

import java.io.DataOutput;
import java.io.IOException;

public class ByteDataOutput implements DataOutput, ByteOutput {
    private final DataOutput wrapped;

    public ByteDataOutput(DataOutput dataInput) {
        this.wrapped = dataInput;
    }

    @Override
    public void write(int b) {
        try {
            wrapped.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(byte[] b) {
        try {
            wrapped.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(byte[] b, int off, int len) {
        try {
            wrapped.write(b, off, len);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeBoolean(boolean v) {
        try {
            wrapped.writeBoolean(v);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeByte(int v) {
        try {
            wrapped.writeByte(v);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeShort(int v) {
        try {
            wrapped.writeShort(v);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeChar(int v) {
        try {
            wrapped.writeChar(v);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeInt(int v) {
        try {
            wrapped.writeInt(v);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeLong(long v) {
        try {
            wrapped.writeLong(v);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeFloat(float v) {
        try {
            wrapped.writeFloat(v);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeDouble(double v) {
        try {
            wrapped.writeDouble(v);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeBytes(String s) {
        try {
            wrapped.writeBytes(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeChars(String s) {
        try {
            wrapped.writeChars(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeString(String s) {
        writeUTF(s);
    }

    @Override
    public void writeUTF(String s) {
        try {
            wrapped.writeUTF(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
