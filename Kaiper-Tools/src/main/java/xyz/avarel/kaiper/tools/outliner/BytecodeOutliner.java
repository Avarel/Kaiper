/*
 *  Copyright 2017 An Tran and Adrian Todt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package xyz.avarel.kaiper.tools.outliner;

import xyz.avarel.kaiper.bytecode.BytecodeUtils;
import xyz.avarel.kaiper.bytecode.io.KDataInput;
import xyz.avarel.kaiper.bytecode.io.KDataInputStream;
import xyz.avarel.kaiper.bytecode.opcodes.KOpcodes;
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

    public void doOutline(KDataInput input, Writer writer) throws IOException {
        readAndDump(input, new PrintWriter(writer, true));
    }

    public void doOutline(KDataInput input, OutputStream out) throws IOException {
        readAndDump(input, new PrintWriter(out, true));
    }

    public void doOutline(InputStream input, Writer writer) throws IOException {
        readAndDump(new KDataInputStream(input), new PrintWriter(writer, true));
    }

    public void doOutline(InputStream input, OutputStream out) throws IOException {
        readAndDump(new KDataInputStream(input), new PrintWriter(out, true));
    }

    public void doOutline(byte[] input, Writer writer) throws IOException {
        readAndDump(new KDataInputStream(new ByteArrayInputStream(input)), new PrintWriter(writer, true));
    }

    public void doOutline(byte[] input, OutputStream out) throws IOException {
        readAndDump(new KDataInputStream(new ByteArrayInputStream(input)), new PrintWriter(out, true));
    }

    public String doOutline(KDataInput input) throws IOException {
        StringWriter writer = new StringWriter();
        doOutline(input, writer);
        return writer.toString();
    }

    public String doOutline(InputStream input) throws IOException {
        return doOutline((KDataInput) new KDataInputStream(input));
    }

    public String doOutline(byte[] input) throws IOException {
        return doOutline(new ByteArrayInputStream(input));
    }

    //endregion

    //region DELEGATES WITH COMPRESSION

    public void doOutlineCompressed(InputStream input, Writer writer) throws IOException {
        try (GZIPInputStream gz = new GZIPInputStream(input)) {
            readAndDump(new KDataInputStream(gz), new PrintWriter(writer, true));
        }
    }

    public void doOutlineCompressed(InputStream input, OutputStream out) throws IOException {
        try (GZIPInputStream gz = new GZIPInputStream(input)) {
            readAndDump(new KDataInputStream(gz), new PrintWriter(out, true));
        }
    }

    public void doOutlineCompressed(byte[] input, Writer writer) throws IOException {
        try (GZIPInputStream gz = new GZIPInputStream(new ByteArrayInputStream(input))) {
            readAndDump(new KDataInputStream(gz), new PrintWriter(writer, true));
        }
    }

    public void doOutlineCompressed(byte[] input, OutputStream out) throws IOException {
        try (GZIPInputStream gz = new GZIPInputStream(new ByteArrayInputStream(input))) {
            readAndDump(new KDataInputStream(gz), new PrintWriter(out, true));
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

    private void readAndDump(KDataInput input, PrintWriter out) throws IOException {
        headerCheck(input, out);
        versionHeaderAndCheck(input, out);
        List<String> stringPool = stringPool(input, out);
        out.println("Main: {");
        KOpcodes.READER.read(new OutlineProcessor(options, out, stringPool), input);
        out.println("}");
    }

    //endregion

    //region HEADERS

    private void headerCheck(KDataInput input, PrintWriter out) throws IOException {
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

    private void versionHeaderAndCheck(KDataInput input, PrintWriter out) throws IOException {
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

    private List<String> stringPool(KDataInput input, PrintWriter out) throws IOException {
        int poolSize = input.readShort();
        List<String> constants = new ArrayList<>(poolSize);

        for (int i = 0; i < poolSize; i++) {
            constants.add(input.readString());
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
