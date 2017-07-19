package xyz.avarel.aje.vm;

import xyz.avarel.aje.bytecode.walker.BufferWalker;
import xyz.avarel.aje.bytecode.walker.BytecodeBatchReader;
import xyz.avarel.aje.bytecode.walker.BytecodeWalkerAdapter;
import xyz.avarel.aje.scope.Scope;
import xyz.avarel.aje.vm.compiled.CompiledScopedExecution;
import xyz.avarel.aje.vm.runtime.functions.CompiledParameter;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

class FunctionParamWalker extends BytecodeWalkerAdapter {
    LinkedList<CompiledParameter> parameters = new LinkedList<>();

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
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            reader.walkInsts(input, new BufferWalker(new DataOutputStream(buffer)), stringPool, depth + 1);
            byte[] bytecode = buffer.toByteArray();
            Scope scope = parentWalker.getScope().subPool();

            parameters.push(new CompiledParameter(name, new CompiledScopedExecution(bytecode, reader, stringPool, depth, scope), isRest));
        } else {
            parameters.push(new CompiledParameter(name, null, isRest));
        }
    }
}
