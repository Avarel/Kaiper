package xyz.avarel.kaiper.tools.outliner;

import xyz.avarel.kaiper.bytecode.BytecodeUtils;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.exceptions.KaiperException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static xyz.avarel.kaiper.bytecode.KaiperBytecode.*;

public class BytecodeOutliner {
    public static final OutlineOptions DEFAULT_OPTIONS = new OutlineOptions();

    private final OutlineOptions options;

    public BytecodeOutliner() {
        this(DEFAULT_OPTIONS);
    }

    public BytecodeOutliner(OutlineOptions options) {
        this.options = new OutlineOptions(options);
    }

    //region DELEGATES

    public void doOutline(DataInput input, Writer writer) throws IOException {
        readAndDump(input, new PrintWriter(writer, true));
    }

    public void doOutline(DataInput input, OutputStream out) throws IOException {
        readAndDump(input, new PrintWriter(out, true));
    }

    public void doOutline(InputStream input, Writer writer) throws IOException {
        readAndDump(new DataInputStream(input), new PrintWriter(writer, true));
    }

    public void doOutline(InputStream input, OutputStream out) throws IOException {
        readAndDump(new DataInputStream(input), new PrintWriter(out, true));
    }

    public void doOutline(byte[] input, Writer writer) throws IOException {
        readAndDump(new DataInputStream(new ByteArrayInputStream(input)), new PrintWriter(writer, true));
    }

    public void doOutline(byte[] input, OutputStream out) throws IOException {
        readAndDump(new DataInputStream(new ByteArrayInputStream(input)), new PrintWriter(out, true));
    }

    public String doOutline(DataInput input) throws IOException {
        StringWriter writer = new StringWriter();
        doOutline(input, writer);
        return writer.toString();
    }

    public String doOutline(InputStream input) throws IOException {
        return doOutline((DataInput) new DataInputStream(input));
    }

    public String doOutline(byte[] input) throws IOException {
        return doOutline(new ByteArrayInputStream(input));
    }

    //endregion

    //region DELEGATES WITH COMPRESSION

    public void doOutlineCompressed(InputStream input, Writer writer) throws IOException {
        try (GZIPInputStream gz = new GZIPInputStream(input)) {
            readAndDump(new DataInputStream(gz), new PrintWriter(writer, true));
        }
    }

    public void doOutlineCompressed(InputStream input, OutputStream out) throws IOException {
        try (GZIPInputStream gz = new GZIPInputStream(input)) {
            readAndDump(new DataInputStream(gz), new PrintWriter(out, true));
        }
    }

    public void doOutlineCompressed(byte[] input, Writer writer) throws IOException {
        try (GZIPInputStream gz = new GZIPInputStream(new ByteArrayInputStream(input))) {
            readAndDump(new DataInputStream(gz), new PrintWriter(writer, true));
        }
    }

    public void doOutlineCompressed(byte[] input, OutputStream out) throws IOException {
        try (GZIPInputStream gz = new GZIPInputStream(new ByteArrayInputStream(input))) {
            readAndDump(new DataInputStream(gz), new PrintWriter(out, true));
        }
    }

    public String doOutlineCompressed(InputStream input) throws IOException {
        StringWriter writer = new StringWriter();
        doOutlineCompressed(input, writer);
        return writer.toString();
    }

    public String doOutlineCompressed(byte[] input) throws IOException {
        return doOutlineCompressed(new ByteArrayInputStream(input));
    }

    //endregion

    //region MAIN METHOD

    private void readAndDump(DataInput input, PrintWriter out) throws IOException {
        headerCheck(input, out);
        versionHeaderAndCheck(input, out);
        List<String> stringPool = stringPool(input, out);
        out.println("Main: {");
        new OpcodeReader(opcodes, foreignOpcodes).read(input, new OutlineProcessor(options, out, 0, stringPool), stringPool, 0);
        out.println("}");
    }

    //endregion

    //region HEADERS

    private void headerCheck(DataInput input, PrintWriter out) throws IOException {
        byte k = input.readByte();
        byte a = input.readByte();
        byte i = input.readByte();

        if (options.skipHeaderChecking()) return;

        if (k != 'K' || a != 'a' || i != 'i') {
            String hexKaiper = "0x" + BytecodeUtils.toHex(k, a, i);

            if (!options.dontExitOnHeaderError()) throw new KaiperException("Invalid Header " + hexKaiper + "");

            String rightHex = "0x" + BytecodeUtils.toHex(IDENTIFIER_BYTES);
            out.println("(Invalid Header " + hexKaiper + ", was expecting " + rightHex + " (Kai))");
            out.println();
        }
    }

    private void versionHeaderAndCheck(DataInput input, PrintWriter out) throws IOException {
        int versionMajor = input.readByte(), versionMinor = input.readByte();

        //Skip Header
        if (!options.skipVersionHeader()) out.print("Version: Kai" + versionMajor + "." + versionMinor);

        //Skip Check
        if (!options.skipVersionChecking()) {
            if (versionMajor != BYTECODE_VERSION_MAJOR || versionMinor > BYTECODE_VERSION_MINOR) {
                out.print(options.skipVersionHeader() ? "(Bytecode Viewing might be incorrect due to unmatched version)" :
                        " (Bytecode viewing might be incorrect)"
                );
            }
        }

        if (!options.skipVersionHeader()) {
            out.println();
            out.println();
        }
    }

    private List<String> stringPool(DataInput input, PrintWriter out) throws IOException {
        int poolSize = input.readShort();
        List<String> constants = new ArrayList<>(poolSize);

        for (int i = 0; i < poolSize; i++) {
            constants.add(input.readUTF());
        }

        if (!options.skipStringPool()) {
            out.print("ConstantPool: ");

            if (options.dontPrintPoolValues()) {
                out.println("(Size: " + constants.size() + ")");
            } else {
                out.println('{');

                int x = constants.size();
                String formatString = (options.use4Spaces() ? "    " : '\t') + "[%0" + (x <= 9 ? 1 : x <= 99 ? 2 : x <= 999 ? 3 : x <= 9999 ? 4 : 5) + "d]: %s\n";

                for (int i = 0; i < constants.size(); i++) {
                    out.printf(formatString, i, BytecodeUtils.escape(constants.get(i)));
                }
            }

            out.println("}");
            out.println();
        }

        return constants;
    }

    //endregion
}
