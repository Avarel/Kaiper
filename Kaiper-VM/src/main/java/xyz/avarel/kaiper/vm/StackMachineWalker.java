package xyz.avarel.kaiper.vm;

import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.bytecode.reader.walker.BufferWalker;
import xyz.avarel.kaiper.bytecode.reader.walker.DummyWalker;
import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.runtime.Bool;
import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.functions.Func;
import xyz.avarel.kaiper.runtime.types.Type;
import xyz.avarel.kaiper.scope.Scope;
import xyz.avarel.kaiper.vm.compiled.CompiledExecution;
import xyz.avarel.kaiper.vm.runtime.functions.CompiledFunc;
import xyz.avarel.kaiper.vm.runtime.types.CompiledConstructor;
import xyz.avarel.kaiper.vm.runtime.types.CompiledType;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class StackMachineWalker {
    public final Scope scope;
    public final Stack<Obj> stack = new Stack<>();
    private long timeout = System.currentTimeMillis() + GlobalVisitorSettings.MILLISECONDS_LIMIT;

    public StackMachineWalker(Scope scope) {
        this.scope = scope;
    }

    public StackMachineWalker(StackMachineWalker parent, Scope scope) {
        this.timeout = parent.timeout;
        this.scope = scope;
    }


    public void opcodeNewFunction(DataInput in, OpcodeReader reader, List<String> stringPool, int depth) throws IOException {
        checkTimeout();
        String name = stringPool.get(in.readUnsignedShort());

        FunctionParamWalker paramWalker = new FunctionParamWalker(this);
        reader.read(in, paramWalker, stringPool, depth + 1);
        checkTimeout();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        reader.read(in, new BufferWalker(new DataOutputStream(buffer)), stringPool, depth + 1);
        checkTimeout();

        Func func = new CompiledFunc(
                name.isEmpty() ? null : name,
                paramWalker.parameters,
                new CompiledExecution(buffer.toByteArray(), reader, stringPool, depth + 1, this),
                scope.subPool()
        );

        if (func.getName() != null) scope.declare(func.getName(), func);

        stack.push(func);
    }

    public void opcodeNewType(DataInput in, OpcodeReader reader, List<String> stringPool, int depth) throws IOException {
        checkTimeout();
        String name = stringPool.get(in.readUnsignedShort());

        Scope typeScope = scope.subPool();

        //Block 1: Parameters
        FunctionParamWalker paramWalker = new FunctionParamWalker(this);
        reader.read(in, paramWalker, stringPool, depth + 1);
        checkTimeout();

        //Block 2: SuperType
        reader.read(in, this, stringPool, depth + 1);
        checkTimeout();
        Obj superType = stack.pop();

        if (superType != Obj.TYPE && !(superType instanceof CompiledType)) {
            throw new ComputeException(superType + " can not be extended");
        }

        //Block 3: SuperParameters
        int size = stack.size();
        reader.read(in, this, stringPool, depth + 1);
        checkTimeout();

        LinkedList<Obj> superParams = new LinkedList<>();
        for (int i = stack.size(); i > size; i--) {
            superParams.push(stack.pop());
        }

        //Block 4: Inner
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        reader.read(in, new BufferWalker(new DataOutputStream(buffer)), stringPool, depth + 1);
        checkTimeout();

        //Building
        CompiledConstructor constructor = new CompiledConstructor(
                paramWalker.parameters, superParams, new CompiledExecution(buffer.toByteArray(), reader, stringPool, depth + 1, this), typeScope
        );

        CompiledType type = new CompiledType((Type) superType, name, constructor);

        scope.declare(name, type);

        stack.push(type);
    }

    public void opcodeConditional(DataInput in, OpcodeReader reader, List<String> stringPool, int depth) throws IOException {
        checkTimeout();
        boolean hasElseBranch = in.readBoolean();
        reader.read(in, this, stringPool, depth + 1);
        checkTimeout();

        if (stack.pop() == Bool.TRUE) {
            reader.read(in, this, stringPool, depth + 1);
            checkTimeout();
            if (hasElseBranch) reader.read(in, DummyWalker.INSTANCE, stringPool, depth + 1);
        } else {
            reader.read(in, DummyWalker.INSTANCE, stringPool, depth + 1);
            checkTimeout();
            if (hasElseBranch) reader.read(in, this, stringPool, depth + 1);
            else stack.push(Null.VALUE);
        }
    }


    public void opcodeForEach(DataInput in, OpcodeReader reader, List<String> stringPool, int depth) throws IOException {
        checkTimeout();
        String variant = stringPool.get(in.readUnsignedShort());

        reader.read(in, this, stringPool, depth + 1);
        checkTimeout();
        Obj iterObj = stack.pop();

        if (!(iterObj instanceof Iterable)) {
            stack.push(Null.VALUE);
            return;
        }

        Iterable iterable = (Iterable) iterObj;

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        reader.read(in, new BufferWalker(new DataOutputStream(buffer)), stringPool, depth + 1);
        checkTimeout();
        byte[] bytecode = buffer.toByteArray();

        int iter = 0;
        for (Object var : iterable) {
            GlobalVisitorSettings.checkIterationLimit(iter);

            if (!(var instanceof Obj)) {
                throw new ComputeException("Items in iterable do not implement Obj interface");
            }

            Scope copy = scope.subPool();
            copy.declare(variant, (Obj) var);
            reader.read(new DataInputStream(new ByteArrayInputStream(bytecode)), new StackMachineWalker(this, copy), stringPool, depth + 1);
            checkTimeout();
            iter++;
        }

        stack.push(Null.VALUE);
    }

    public void resetTimeout() {
        timeout = System.currentTimeMillis() + GlobalVisitorSettings.MILLISECONDS_LIMIT;
    }

    public void checkTimeout() {
        GlobalVisitorSettings.checkTimeout(timeout);
    }
}
