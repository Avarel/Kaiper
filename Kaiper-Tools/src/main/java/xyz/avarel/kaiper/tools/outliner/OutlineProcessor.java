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
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.bytecode.reader.processors.MultiOpcodeProcessorAdapter;
import xyz.avarel.kaiper.bytecode.reader.processors.ReadResult;

import java.io.PrintWriter;
import java.util.List;

import static xyz.avarel.kaiper.bytecode.reader.processors.ReadResult.*;

public class OutlineProcessor extends MultiOpcodeProcessorAdapter {
    private final OutlineOptions options;
    private final PrintWriter out;
    private final int depth;
    private final List<String> stringPool;

    public OutlineProcessor(OutlineOptions options, PrintWriter out, int depth, List<String> stringPool) {
        this.options = options;
        this.out = out;
        this.depth = depth;
        this.stringPool = stringPool;
    }

    @Override
    public ReadResult process(OpcodeReader reader, Opcode opcode, KDataInput in) {
        beginLine();
        out.print(opcode.name());
        ReadResult result = super.process(reader, opcode, in);
        out.println();
        return result;
    }

    @Override
    public ReadResult opcodeEnd(OpcodeReader reader, KDataInput in) {
        int endId = in.readUnsignedShort();
        boolean valid = endId == (depth - 1);

        out.print(" " + endId);
        if (!valid) out.write(" (Invalid)");

        return ENDED;
    }

    @Override
    public ReadResult opcodeLineNumber(OpcodeReader reader, KDataInput in) {
        long ln = in.readLong();
        out.print(" ");
        out.print(ln);
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeBreakpoint(OpcodeReader reader, KDataInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeReturn(OpcodeReader reader, KDataInput in) {
        return RETURNED;
    }

    @Override
    public ReadResult opcodeDup(OpcodeReader reader, KDataInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodePop(OpcodeReader reader, KDataInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNullConstant(OpcodeReader reader, KDataInput in) {
        return null;
    }

    @Override
    public ReadResult opcodeBooleanConstant(OpcodeReader reader, boolean value, KDataInput in) {
        return null;
    }

    @Override
    public ReadResult opcodeIntConstant(OpcodeReader reader, KDataInput in) {
        int intValue = in.readInt();
        out.print(" ");
        out.print(intValue);
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeDecimalConstant(OpcodeReader reader, KDataInput in) {
        double doubleValue = in.readDouble();
        out.print(" ");
        out.print(doubleValue);
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeStringConstant(OpcodeReader reader, KDataInput in) {
        int constIndex = in.readUnsignedShort();
        beginLine();
        out.print(" ");
        writeString(stringPool, constIndex);
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewArray(OpcodeReader reader, KDataInput in) {
        int intValue = in.readInt();
        out.print(" ");
        out.print(intValue);
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewDictionary(OpcodeReader reader, KDataInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewRange(OpcodeReader reader, KDataInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewFunction(OpcodeReader reader, KDataInput in) {
        //TODO
        OutlineProcessor walker = new OutlineProcessor(options, out, this.depth + 1, stringPool);
        int constIndex = in.readUnsignedShort();

        out.print(" ");
        writeString(stringPool, constIndex);
        out.println(": (");

        reader.read(input, walker, stringPool, depth + 1);

        beginLine();
        out.println("), {");

        reader.read(input, walker, stringPool, depth + 1);

        beginLine();
        out.println("}");
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewModule(OpcodeReader reader, KDataInput in) {
        //TODO
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewType(OpcodeReader reader, KDataInput in) {
        //TODO
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewTuple(OpcodeReader reader, KDataInput in) {
        //TODO
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeDeclare(OpcodeReader reader, KDataInput in) {
        int constIndex = in.readUnsignedShort();
        out.print(" ");
        writeString(stringPool, constIndex);
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeAssign(OpcodeReader reader, KDataInput in) {
        boolean parented = in.readBoolean();
        int constIndex = in.readUnsignedShort();

        out.printf(" %s ", parented);
        writeString(stringPool, constIndex);
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeIdentifier(OpcodeReader reader, KDataInput in) {
        boolean parented = in.readBoolean();
        int constIndex = in.readUnsignedShort();

        out.printf(" %s ", parented);
        writeString(stringPool, constIndex);
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeBindDeclare(OpcodeReader reader, KDataInput in) {
        //TODO
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeBindAssign(OpcodeReader reader, KDataInput in) {
        //TODO
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeInvoke(OpcodeReader reader, KDataInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeUnaryOperation(OpcodeReader reader, KDataInput in) {
        int type = in.readByte();
        out.print(" " + type);
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeBinaryOperation(OpcodeReader reader, KDataInput in) {
        int type = in.readByte();
        out.print(" " + type);
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeSliceOperation(OpcodeReader reader, KDataInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeArrayGet(OpcodeReader reader, KDataInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeArraySet(OpcodeReader reader, KDataInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeConditional(OpcodeReader reader, KDataInput in) {
        //TODO
        OutlineProcessor walker = new OutlineProcessor(options, out, this.depth + 1, stringPool);

        boolean hasElseBranch = in.readBoolean();
        beginLine();
        out.printf(" %s ", hasElseBranch);
        out.println(": (");
        reader.read(input, walker, stringPool, depth + 1);
        beginLine();
        out.println("), {");
        reader.read(input, walker, stringPool, depth + 1);
        if (hasElseBranch) {
            beginLine();
            out.println("}, {");
            reader.read(input, walker, stringPool, depth + 1);
        }
        beginLine();
        out.println("}");
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeForEach(OpcodeReader reader, KDataInput in) {
        //TODO
        OutlineProcessor walker = new OutlineProcessor(options, out, this.depth + 1, stringPool);

        int constIndex = in.readUnsignedShort();
        out.print(" ");
        writeString(stringPool, constIndex);
        out.println(": (");
        reader.read(input, walker, stringPool, depth + 1);
        beginLine();
        out.println("), {");
        reader.read(input, walker, stringPool, depth + 1);
        beginLine();
        out.println("}");
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeWhile(OpcodeReader reader, KDataInput in) {
        //TODO
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeVariablePattern(OpcodeReader reader, KDataInput in) {
        return null;
    }

    @Override
    public ReadResult opcodeTuplePattern(OpcodeReader reader, KDataInput in) {
        return null;
    }

    @Override
    public ReadResult opcodeRestPattern(OpcodeReader reader, KDataInput in) {
        return null;
    }

    @Override
    public ReadResult opcodeValuePattern(OpcodeReader reader, KDataInput in) {
        return null;
    }

    @Override
    public ReadResult opcodeDefaultPattern(OpcodeReader reader, KDataInput in) {
        return null;
    }

    //region HELPER METHODS

    private void writeString(List<String> stringPool, int constIndex) {
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
    }

    private void beginLine() {
        String tab = options.use4Spaces() ? "    " : "\t";
        for (int i = 0; i < depth + 1; i++) out.print(tab);
    }

    //endregion
}
