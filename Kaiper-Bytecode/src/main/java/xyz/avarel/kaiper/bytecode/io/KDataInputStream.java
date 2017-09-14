package xyz.avarel.kaiper.bytecode.io;

import java.io.*;

/**
 * Yes, this is a total bootleg of the {@link DataInputStream} class, but with a lot of try-catches.
 *
 * @author AdrianTodt
 */
public class KDataInputStream extends DelegatedInputStream implements KDataInput {
    private static String readString(KDataInput in) throws IOException {
        int utflen = in.readUnsignedShort();
        byte[] bytearr = null;
        char[] chararr = null;
        if (in instanceof KDataInputStream) {
            KDataInputStream dis = (KDataInputStream) in;
            if (dis.bytearr.length < utflen) {
                dis.bytearr = new byte[utflen * 2];
                dis.chararr = new char[utflen * 2];
            }
            chararr = dis.chararr;
            bytearr = dis.bytearr;
        } else {
            bytearr = new byte[utflen];
            chararr = new char[utflen];
        }

        int c, char2, char3;
        int count = 0;
        int chararr_count = 0;

        in.readFully(bytearr, 0, utflen);

        while (count < utflen) {
            c = (int) bytearr[count] & 0xff;
            if (c > 127) break;
            count++;
            chararr[chararr_count++] = (char) c;
        }

        while (count < utflen) {
            c = (int) bytearr[count] & 0xff;
            switch (c >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:

                    count++;
                    chararr[chararr_count++] = (char) c;
                    break;
                case 12:
                case 13:

                    count += 2;
                    if (count > utflen)
                        throw new UTFDataFormatException(
                                "malformed input: partial character at end");
                    char2 = (int) bytearr[count - 1];
                    if ((char2 & 0xC0) != 0x80)
                        throw new UTFDataFormatException(
                                "malformed input around byte " + count);
                    chararr[chararr_count++] = (char) (((c & 0x1F) << 6) |
                            (char2 & 0x3F));
                    break;
                case 14:

                    count += 3;
                    if (count > utflen)
                        throw new UTFDataFormatException(
                                "malformed input: partial character at end");
                    char2 = (int) bytearr[count - 2];
                    char3 = (int) bytearr[count - 1];
                    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
                        throw new UTFDataFormatException(
                                "malformed input around byte " + (count - 1));
                    chararr[chararr_count++] = (char) (((c & 0x0F) << 12) |
                            ((char2 & 0x3F) << 6) |
                            ((char3 & 0x3F)));
                    break;
                default:

                    throw new UTFDataFormatException(
                            "malformed input around byte " + count);
            }
        }
        // The number of chars produced may be less than utflen
        return new String(chararr, 0, chararr_count);
    }

    private byte bytearr[] = new byte[80];
    private char chararr[] = new char[80];
    private byte readBuffer[] = new byte[8];

    public KDataInputStream(InputStream in) {
        super(in);
    }

    public final int read(byte b[]) {
        try {
            return in.read(b, 0, b.length);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public final int read(byte b[], int off, int len) {
        try {
            return in.read(b, off, len);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public final KDataInput readFully(byte b[]) {
        readFully(b, 0, b.length);

        return this;
    }

    public final KDataInput readFully(byte b[], int off, int len) {
        try {
            if (len < 0)
                throw new IndexOutOfBoundsException();
            int n = 0;
            while (n < len) {
                int count = in.read(b, off + n, len - n);
                if (count < 0)
                    throw new EOFException();
                n += count;
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return this;
    }

    public final int skipBytes(int n) {
        try {
            int total = 0;
            int cur = 0;

            while ((total < n) && ((cur = (int) in.skip(n - total)) > 0)) {
                total += cur;
            }

            return total;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public final boolean readBoolean() {
        try {
            int ch = in.read();
            if (ch < 0)
                throw new EOFException();
            return (ch != 0);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public final byte readByte() {
        try {
            int ch = in.read();
            if (ch < 0)
                throw new EOFException();
            return (byte) (ch);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public final int readUnsignedByte() {
        try {
            int ch = in.read();
            if (ch < 0)
                throw new EOFException();
            return ch;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public final short readShort() {
        try {
            int ch1 = in.read();
            int ch2 = in.read();
            if ((ch1 | ch2) < 0)
                throw new EOFException();
            return (short) ((ch1 << 8) + (ch2));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public final int readUnsignedShort() {
        try {
            int ch1 = in.read();
            int ch2 = in.read();
            if ((ch1 | ch2) < 0)
                throw new EOFException();
            return (ch1 << 8) + (ch2);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public final char readChar() {
        try {
            int ch1 = in.read();
            int ch2 = in.read();
            if ((ch1 | ch2) < 0)
                throw new EOFException();
            return (char) ((ch1 << 8) + (ch2));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public final int readInt() {
        try {
            int ch1 = in.read();
            int ch2 = in.read();
            int ch3 = in.read();
            int ch4 = in.read();
            if ((ch1 | ch2 | ch3 | ch4) < 0)
                throw new EOFException();
            return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public final long readLong() {
        readFully(readBuffer, 0, 8);
        return (((long) readBuffer[0] << 56) +
                ((long) (readBuffer[1] & 255) << 48) +
                ((long) (readBuffer[2] & 255) << 40) +
                ((long) (readBuffer[3] & 255) << 32) +
                ((long) (readBuffer[4] & 255) << 24) +
                ((readBuffer[5] & 255) << 16) +
                ((readBuffer[6] & 255) << 8) +
                ((readBuffer[7] & 255)));
    }

    public final float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    public final double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    public final String readString() {
        try {
            return readString(this);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public InputStream getInputStream() {
        return in;
    }
}
