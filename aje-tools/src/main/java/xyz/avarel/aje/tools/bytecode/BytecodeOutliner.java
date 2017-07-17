package xyz.avarel.aje.tools.bytecode;

import xyz.avarel.aje.bytecode.BytecodeUtils;
import xyz.avarel.aje.bytecode.Opcodes;
import xyz.avarel.aje.exceptions.AJEException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static xyz.avarel.aje.bytecode.AJEBytecode.BYTECODE_VERSION_MAJOR;
import static xyz.avarel.aje.bytecode.AJEBytecode.BYTECODE_VERSION_MINOR;
import static xyz.avarel.aje.bytecode.AJEBytecode.IDENTIFIER;

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
        mainOutline(input, out, stringPool, 0);
        out.println("}");
    }

    //endregion

    //region HEADERS

    private void headerCheck(DataInput input, PrintWriter out) throws IOException {
        byte a = input.readByte();
        byte j = input.readByte();
        byte e = input.readByte();

        if (options.skipHeaderChecking()) return;

        if (a != 'A' || j != 'J' || e != 'E') {
            String hexAJE = "0x" + BytecodeUtils.toHex(new byte[]{a, j, e});

            if (!options.dontExitOnHeaderError()) throw new AJEException("Invalid Header " + hexAJE + "");

            String rightHex = "0x" + BytecodeUtils.toHex(IDENTIFIER);
            out.println("(Invalid Header " + hexAJE + ", was expecting " + rightHex + " (AJE))");
            out.println();
        }
    }

    private void versionHeaderAndCheck(DataInput input, PrintWriter out) throws IOException {
        int versionMajor = input.readByte(), versionMinor = input.readByte();

        //Skip Header
        if (!options.skipVersionHeader()) out.print("Version: AJE" + versionMajor + "." + versionMinor);

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

    //region INSTRUCTION READING

    private void mainOutline(DataInput input, PrintWriter out, List<String> stringPool, int depth) throws IOException {
        while (nextInst(input, out, stringPool, depth)) ;
    }

    private boolean nextInst(DataInput input, PrintWriter out, List<String> stringPool, int depth) throws IOException {
        byte bytecode = input.readByte();
        Opcodes opcode;
        switch (opcode = Opcodes.byId(bytecode)) {
            case END: {
                int endId = input.readShort();
                boolean valid = endId == (depth - 1);

                if (options.explicitEnd() || !valid) {
                    beginLine(out, depth);
                    out.print("END ");
                    out.print(endId);
                    if (!valid) out.write(" (Invalid)");
                    out.println();
                }

                return !valid;
            }
            case I_CONST: {
                int intValue = input.readInt();
                beginLine(out, depth);
                out.print("I_CONST ");
                out.println(intValue);
                break;
            }
            case D_CONST: {
                double doubleValue = input.readDouble();
                beginLine(out, depth);
                out.print("D_CONST ");
                out.println(doubleValue);
                break;
            }
            case S_CONST: {
                int constIndex = input.readShort();
                beginLine(out, depth);
                out.print("S_CONST ");
                writeString(out, stringPool, constIndex);
                break;
            }
            case NEW_FUNCTION: {
                int constIndex = input.readShort();
                beginLine(out, depth);
                out.print("NEW_FUNCTION ");
                writeString(out, false, stringPool, constIndex);
                out.println(": (");
                mainOutline(input, out, stringPool, depth + 1);
                beginLine(out, depth);
                out.println("), {");
                mainOutline(input, out, stringPool, depth + 1);
                beginLine(out, depth);
                out.println("}");
                break;
            }
            case FUNCTION_DEF_PARAM: {
                int modifiers = input.readByte();
                int constIndex = input.readShort();
                beginLine(out, depth);
                out.printf("FUNCTION_DEF_PARAM %d ", modifiers);
                writeString(out, false, stringPool, constIndex);

                boolean hasDefaultValue = (modifiers & 1) == 1;

                if (hasDefaultValue) {
                    out.println(": {");
                    mainOutline(input, out, stringPool, depth + 1);
                    beginLine(out, depth);
                    out.print("}");
                }

                out.println();
                break;
            }
            case INVOKE: {
                int pCount = input.readByte();
                beginLine(out, depth);
                out.print("INVOKE ");
                out.println(pCount);
                break;
            }
            case DECLARE: {
                int constIndex = input.readShort();
                beginLine(out, depth);
                out.print("DECLARE ");
                writeString(out, stringPool, constIndex);
                break;
            }
            case UNARY_OPERATION:
            case BINARY_OPERATION: {
                int type = input.readByte();
                beginLine(out, depth);
                out.print(opcode.name());
                out.print(" ");
                out.println(type);
                break;
            }
            case IDENTIFIER:
            case ASSIGN: {
                boolean parented = input.readBoolean();
                int constIndex = input.readShort();
                beginLine(out, depth);
                out.printf("%s %s ", opcode.name(), parented);
                writeString(out, stringPool, constIndex);
                break;
            }
            case CONDITIONAL: {
                boolean hasElseBranch = input.readBoolean();
                beginLine(out, depth);
                out.printf("CONDITIONAL %s ", hasElseBranch);
                out.println(": (");
                mainOutline(input, out, stringPool, depth + 1);
                beginLine(out, depth);
                out.println("), {");
                mainOutline(input, out, stringPool, depth + 1);
                if (hasElseBranch) {
                    beginLine(out, depth);
                    out.println("}, {");
                    mainOutline(input, out, stringPool, depth + 1);
                }
                beginLine(out, depth);
                out.println("}");
                break;
            }
            case FOR_EACH: {
                int constIndex = input.readShort();
                beginLine(out, depth);
                out.print("FOR_EACH ");
                writeString(out, false, stringPool, constIndex);
                out.println(": (");
                mainOutline(input, out, stringPool, depth + 1);
                beginLine(out, depth);
                out.println("), {");
                mainOutline(input, out, stringPool, depth + 1);
                beginLine(out, depth);
                out.println("}");
                break;
            }
            case NEW_ARRAY:
            case NEW_DICTIONARY:
            case NEW_RANGE:
            case RETURN:
            case U_CONST:
            case B_CONST_TRUE:
            case B_CONST_FALSE:
            case SLICE_OPERATION:
            case GET:
            case SET:
            case DUP:
            case POP:
            default: {
                beginLine(out, depth);
                out.println(opcode.name());
                break;
            }
        }

        return true;
    }

    //endregion

    //region HELPER METHODS

    private void writeString(PrintWriter out, List<String> stringPool, int constIndex) {
        writeString(out, true, stringPool, constIndex);
    }

    private void writeString(PrintWriter out, boolean newLine, List<String> stringPool, int constIndex) {
        if (options.inlineStrings()) {
            out.print(BytecodeUtils.escape(stringPool.get(constIndex)));
        } else if (options.skipStringPool()) {
            out.print(constIndex);
            out.print(" [");
            out.print(BytecodeUtils.escape(stringPool.get(constIndex)));
            out.print("]");
        } else {
            out.print(constIndex);
        }

        if (newLine) out.println();
    }

    private void beginLine(PrintWriter out, int depth) {
        depth++;
        for (int i = 0; i < depth; i++) out.print(options.use4Spaces() ? "    " : '\t');
    }

    //endregion
}
