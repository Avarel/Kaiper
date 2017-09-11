package xyz.avarel.kaiper.bytecode.io;


import xyz.avarel.kaiper.bytecode.opcodes.Opcode;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;
import java.io.UncheckedIOException;

/**
 * Yes, this is a total bootleg of the {@link java.io.DataOutputStream} class, but with a lot of try-catches.
 *
 * @author AdrianTodt
 */
public class ByteOutputStream extends DelegatedOutputStream implements ByteOutput {
    private static void writeString(String str, ByteOutput out) throws IOException {
        int strlen = str.length();
        int utflen = 0;
        int c, count = 0;


        for (int i = 0; i < strlen; i++) {
            c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                utflen++;
            } else if (c > 0x07FF) {
                utflen += 3;
            } else {
                utflen += 2;
            }
        }

        if (utflen > 65535)
            throw new UTFDataFormatException(
                    "encoded string too long: " + utflen + " bytes");

        byte[] bytearr = null;
        if (out instanceof ByteOutputStream) {
            ByteOutputStream dos = (ByteOutputStream) out;
            if (dos.bytearr == null || (dos.bytearr.length < (utflen + 2)))
                dos.bytearr = new byte[(utflen * 2) + 2];
            bytearr = dos.bytearr;
        } else {
            bytearr = new byte[utflen + 2];
        }

        bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
        bytearr[count++] = (byte) ((utflen) & 0xFF);

        int i = 0;
        for (i = 0; i < strlen; i++) {
            c = str.charAt(i);
            if (!((c >= 0x0001) && (c <= 0x007F))) break;
            bytearr[count++] = (byte) c;
        }

        for (; i < strlen; i++) {
            c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                bytearr[count++] = (byte) c;

            } else if (c > 0x07FF) {
                bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                bytearr[count++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                bytearr[count++] = (byte) (0x80 | ((c) & 0x3F));
            } else {
                bytearr[count++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
                bytearr[count++] = (byte) (0x80 | ((c) & 0x3F));
            }
        }
        out.writeBytes(bytearr, 0, utflen + 2);
    }

    private byte[] bytearr = null;
    private byte writeBuffer[] = new byte[8];


    public ByteOutputStream(OutputStream out) {
        super(out);
    }

    public synchronized void write(int b) {
        try {
            out.write(b);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void flush() {
        try {
            out.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public final ByteOutputStream writeByte(int v) {
        try {
            out.write(v);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return this;
    }

    @Override
    public ByteOutput writeBytes(byte[] b) {
        try {
            out.write(b);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return this;
    }

    public synchronized ByteOutputStream writeBytes(byte[] b, int off, int len) {
        try {
            out.write(b, off, len);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return this;
    }

    public final ByteOutputStream writeBoolean(boolean v) {
        try {
            out.write(v ? 1 : 0);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return this;
    }

    public final ByteOutputStream writeShort(int v) {
        try {
            out.write((v >>> 8) & 0xFF);
            out.write((v) & 0xFF);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return this;
    }

    public final ByteOutputStream writeChar(int v) {
        try {
            out.write((v >>> 8) & 0xFF);
            out.write((v) & 0xFF);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return this;
    }

    public final ByteOutputStream writeInt(int v) {
        try {
            out.write((v >>> 24) & 0xFF);
            out.write((v >>> 16) & 0xFF);
            out.write((v >>> 8) & 0xFF);
            out.write((v) & 0xFF);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return this;
    }

    public final ByteOutputStream writeLong(long v) {
        writeBuffer[0] = (byte) (v >>> 56);
        writeBuffer[1] = (byte) (v >>> 48);
        writeBuffer[2] = (byte) (v >>> 40);
        writeBuffer[3] = (byte) (v >>> 32);
        writeBuffer[4] = (byte) (v >>> 24);
        writeBuffer[5] = (byte) (v >>> 16);
        writeBuffer[6] = (byte) (v >>> 8);
        writeBuffer[7] = (byte) (v);

        try {
            out.write(writeBuffer, 0, 8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return this;
    }

    public final ByteOutputStream writeFloat(float v) {
        return writeInt(Float.floatToIntBits(v));
    }

    public final ByteOutputStream writeDouble(double v) {
        return writeLong(Double.doubleToLongBits(v));
    }

    public final ByteOutputStream writeBytes(String s) {
        try {
            int len = s.length();
            for (int i = 0; i < len; i++) {
                out.write((byte) s.charAt(i));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return this;
    }

    public final ByteOutputStream writeChars(String s) {
        try {
            int len = s.length();
            for (int i = 0; i < len; i++) {
                int v = s.charAt(i);
                out.write((v >>> 8) & 0xFF);
                out.write((v) & 0xFF);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return this;
    }

    public final ByteOutputStream writeString(String str) {
        try {
            writeString(str, this);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return this;
    }

    @Override
    public ByteOutput writeOpcode(Opcode opcode) {
        return writeByte(opcode.code());
    }

    @Override
    public OutputStream getOutputStream() {
        return out;
    }

}
