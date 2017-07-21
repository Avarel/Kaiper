package xyz.avarel.kaiper.vm;

import xyz.avarel.kaiper.bytecode.walker.BufferWalker;
import xyz.avarel.kaiper.bytecode.walker.BytecodeBatchReader;
import xyz.avarel.kaiper.bytecode.walker.BytecodeWalkerAdapter;
import xyz.avarel.kaiper.scope.Scope;
import xyz.avarel.kaiper.vm.compiled.CompiledScopedExecution;
import xyz.avarel.kaiper.vm.runtime.functions.CompiledParameter;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

class FunctionParamWalker extends BytecodeWalkerAdapter {
    LinkedList<CompiledParameter> parameters = new LinkedList<>();

    private StackMachineWalker parent;

    FunctionParamWalker(StackMachineWalker parent) {
        this.parent = parent;
    }

    @Override
    public void opcodeDefineFunctionParam(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        parent.checkTimeout();
        int modifiers = input.readByte();
        String name = stringPool.get(input.readUnsignedShort());

        boolean isRest = (modifiers & 2) == 2;

        if ((modifiers & 1) == 1) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            reader.walkInsts(input, new BufferWalker(new DataOutputStream(buffer)), stringPool, depth + 1);
            byte[] bytecode = buffer.toByteArray();
            Scope scope = parent.getScope().subPool();

            parameters.push(new CompiledParameter(name, new CompiledScopedExecution(bytecode, reader, stringPool, depth, scope, parent), isRest));
        } else {
            parameters.push(new CompiledParameter(name, null, isRest));
        }
    }
}
