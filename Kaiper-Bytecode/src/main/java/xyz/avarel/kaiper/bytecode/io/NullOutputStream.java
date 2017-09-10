package xyz.avarel.kaiper.bytecode.io;

import java.io.IOException;
import java.io.OutputStream;

//(Will be) Used on some OpcodeConsumers to null output (DummyWalker replacement)
public class NullOutputStream extends OutputStream {
    public static final NullOutputStream INSTANCE = new NullOutputStream();

    private NullOutputStream() {

    }

    @Override
    public void write(int b) throws IOException {
        // ignore
    }

    @Override
    public void write(byte[] b) throws IOException {
        // ignore
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        // ignore
    }
}
