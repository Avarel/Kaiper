package xyz.avarel.aje.tools.bytecode;

import xyz.avarel.aje.bytecode.BytecodeUtils;
import xyz.avarel.aje.bytecode.walker.BytecodeBatchReader;
import xyz.avarel.aje.bytecode.walker.BytecodeWalker;

import java.io.DataInput;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class LogWalker implements BytecodeWalker {
    private final OutlineOptions options;
    private final PrintWriter out;
    private final int depth;

    public LogWalker(OutlineOptions options, PrintWriter out, int depth) {
        this.options = options;
        this.out = out;
        this.depth = depth;
    }

    @Override
    public boolean opcodeEnd(DataInput input, int depth) throws IOException {
        int endId = input.readShort();
        boolean valid = endId == (depth - 1);

        if (options.explicitEnd() || !valid) {
            beginLine();
            out.print("END ");
            out.print(endId);
            if (!valid) out.write(" (Invalid)");
            out.println();
        }

        return !valid;
    }

    @Override
    public void opcodeReturn() throws IOException {
        beginLine();
        out.println("RETURN");
    }

    @Override
    public void opcodeUndefinedConstant() throws IOException {
        beginLine();
        out.println("U_CONST");
    }

    @Override
    public void opcodeBooleanConstantTrue() throws IOException {
        beginLine();
        out.println("B_CONST_TRUE");
    }

    @Override
    public void opcodeBooleanConstantFalse() throws IOException {
        beginLine();
        out.println("B_CONST_FALSE");
    }

    @Override
    public void opcodeIntConstant(DataInput input) throws IOException {
        int intValue = input.readInt();
        beginLine();
        out.print("I_CONST ");
        out.println(intValue);
    }

    @Override
    public void opcodeDecimalConstant(DataInput input) throws IOException {
        double doubleValue = input.readDouble();
        beginLine();
        out.print("D_CONST ");
        out.println(doubleValue);
    }

    @Override
    public void opcodeStringConstant(DataInput input, List<String> stringPool) throws IOException {
        int constIndex = input.readShort();
        beginLine();
        out.print("S_CONST ");
        writeString(stringPool, constIndex);
    }

    @Override
    public void opcodeNewArray() throws IOException {
        beginLine();
        out.println("NEW_ARRAY");
    }

    @Override
    public void opcodeNewDictionary() throws IOException {
        beginLine();
        out.println("NEW_DICTIONARY");
    }

    @Override
    public void opcodeNewRange() throws IOException {
        beginLine();
        out.println("NEW_RANGE");
    }

    @Override
    public void opcodeNewFunction(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        LogWalker walker = new LogWalker(options, out, this.depth + 1);
        int constIndex = input.readShort();

        beginLine();
        out.print("NEW_FUNCTION ");
        writeString(stringPool, constIndex, false);
        out.println(": (");

        reader.walkInsts(input, walker, stringPool, depth + 1);

        beginLine();
        out.println("), {");

        reader.walkInsts(input, walker, stringPool, depth + 1);

        beginLine();
        out.println("}");
    }

    @Override
    public void opcodeDefineFunctionParam(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        int modifiers = input.readByte();
        int constIndex = input.readShort();
        beginLine();
        out.printf("FUNCTION_DEF_PARAM %d ", modifiers);
        writeString(stringPool, constIndex, false);

        boolean hasDefaultValue = (modifiers & 1) == 1;

        if (hasDefaultValue) {
            out.println(": {");
            reader.walkInsts(input, new LogWalker(options, out, this.depth + 1), stringPool, depth + 1);
            beginLine();
            out.print("}");
        }

        out.println();
    }

    @Override
    public void opcodeInvoke(DataInput input) throws IOException {
        int pCount = input.readByte();
        beginLine();
        out.print("INVOKE ");
        out.println(pCount);
    }

    @Override
    public void opcodeDeclare(DataInput input, List<String> stringPool) throws IOException {
        int constIndex = input.readShort();
        beginLine();
        out.print("DECLARE ");
        writeString( stringPool, constIndex);
    }

    @Override
    public void opcodeUnaryOperation(DataInput input) throws IOException {
        int type = input.readByte();
        beginLine();
        out.print("UNARY_OPERATION ");
        out.println(type);
    }

    @Override
    public void opcodeBinaryOperation(DataInput input) throws IOException {
        int type = input.readByte();
        beginLine();
        out.print("BINARY_OPERATION ");
        out.println(type);
    }

    @Override
    public void opcodeSliceOperation() throws IOException {
        beginLine();
        out.println("SLICE_OPERATION");
    }

    @Override
    public void opcodeGet() throws IOException {
        beginLine();
        out.println("GET");
    }

    @Override
    public void opcodeSet() throws IOException {
        beginLine();
        out.println("SET");
    }

    @Override
    public void opcodeIdentifier(DataInput input, List<String> stringPool) throws IOException {
        boolean parented = input.readBoolean();
        int constIndex = input.readShort();
        beginLine();
        out.printf("IDENTIFIER %s ", parented);
        writeString(stringPool, constIndex);
    }

    @Override
    public void opcodeAssign(DataInput input, List<String> stringPool) throws IOException {
        boolean parented = input.readBoolean();
        int constIndex = input.readShort();
        beginLine();
        out.printf("ASSIGN %s ", parented);
        writeString(stringPool, constIndex);
    }

    @Override
    public void opcodeConditional(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        LogWalker walker = new LogWalker(options, out, this.depth + 1);

        boolean hasElseBranch = input.readBoolean();
        beginLine();
        out.printf("CONDITIONAL %s ", hasElseBranch);
        out.println(": (");
        reader.walkInsts(input, walker, stringPool, depth + 1);
        beginLine();
        out.println("), {");
        reader.walkInsts(input, walker, stringPool, depth + 1);
        if (hasElseBranch) {
            beginLine();
            out.println("}, {");
            reader.walkInsts(input, walker, stringPool, depth + 1);
        }
        beginLine();
        out.println("}");
    }

    @Override
    public void opcodeForEach(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        LogWalker walker = new LogWalker(options, out, this.depth + 1);

        int constIndex = input.readShort();
        beginLine();
        out.print("FOR_EACH ");
        writeString(stringPool, constIndex, false);
        out.println(": (");
        reader.walkInsts(input, walker, stringPool, depth + 1);
        beginLine();
        out.println("), {");
        reader.walkInsts(input, walker, stringPool, depth + 1);
        beginLine();
        out.println("}");
    }

    @Override
    public void opcodeDup() throws IOException {
        beginLine();
        out.println("DUP");
    }

    @Override
    public void opcodePop() throws IOException {
        beginLine();
        out.println("POP");
    }

    //region HELPER METHODS

    private void writeString(List<String> stringPool, int constIndex) {
        writeString(stringPool, constIndex, true);
    }

    private void writeString(List<String> stringPool, int constIndex, boolean newLine) {
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

    private void beginLine() {
        for (int i = 0; i < depth + 1; i++) out.print(options.use4Spaces() ? "    " : '\t');
    }

    //endregion
}
