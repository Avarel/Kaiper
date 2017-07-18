package xyz.avarel.aje.vm;

import xyz.avarel.aje.bytecode.walker.BytecodeBatchReader;
import xyz.avarel.aje.bytecode.walker.BytecodeWalkerAdapter;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.functions.Parameter;

import java.io.DataInput;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

class FunctionParamWalker extends BytecodeWalkerAdapter {
    LinkedList<Parameter> parameters = new LinkedList<>();

    private StackMachineWalker parentWalker;

    FunctionParamWalker(StackMachineWalker parentWalker) {
        this.parentWalker = parentWalker;
    }

    @Override
    public void opcodeDefineFunctionParam(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        int modifiers = input.readByte();
        String name = stringPool.get(input.readUnsignedShort());

        boolean isRest = (modifiers & 2) == 2;

        if ((modifiers & 1) == 1) {
            reader.walkInsts(input, parentWalker, stringPool, depth + 1);
            Obj defaultValue = parentWalker.stack.pop();

            parameters.push(Parameter.of(name, defaultValue, isRest));
        } else {
            parameters.push(Parameter.of(name, isRest));
        }
    }
}
