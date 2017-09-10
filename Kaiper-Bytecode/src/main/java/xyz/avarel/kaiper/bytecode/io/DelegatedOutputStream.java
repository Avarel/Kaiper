package xyz.avarel.kaiper.bytecode.io;

import java.io.FilterOutputStream;
import java.io.OutputStream;

public class DelegatedOutputStream extends FilterOutputStream {
    /**
     * Creates an output stream filter built on top of the specified
     * underlying output stream.
     *
     * @param out the underlying output stream to be assigned to
     *            the field <tt>this.out</tt> for later use, or
     *            <code>null</code> if this instance is to be
     *            created without an underlying stream.
     */
    public DelegatedOutputStream(OutputStream out) {
        super(out);
    }

    public void setOutputStream(OutputStream out) {
        this.out = out;
    }
}
