package xyz.avarel.kaiper.vm;

import xyz.avarel.kaiper.bytecode.walker.BufferWalker;
import xyz.avarel.kaiper.bytecode.walker.BytecodeBatchReader;
import xyz.avarel.kaiper.bytecode.walker.BytecodeWalkerAdapter;
import xyz.avarel.kaiper.bytecode.walker.DummyWalker;
import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.exceptions.InvalidBytecodeException;
import xyz.avarel.kaiper.exceptions.ReturnException;
import xyz.avarel.kaiper.operations.BinaryOperatorType;
import xyz.avarel.kaiper.operations.UnaryOperatorType;
import xyz.avarel.kaiper.runtime.Bool;
import xyz.avarel.kaiper.runtime.Null;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Str;
import xyz.avarel.kaiper.runtime.collections.Array;
import xyz.avarel.kaiper.runtime.collections.Dictionary;
import xyz.avarel.kaiper.runtime.collections.Range;
import xyz.avarel.kaiper.runtime.functions.Func;
import xyz.avarel.kaiper.runtime.modules.CompiledModule;
import xyz.avarel.kaiper.runtime.modules.Module;
import xyz.avarel.kaiper.runtime.numbers.Int;
import xyz.avarel.kaiper.runtime.numbers.Number;
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

import static xyz.avarel.kaiper.bytecode.BytecodeUtils.toHex;

public class StackMachineWalker extends BytecodeWalkerAdapter {
    private static final DummyWalker dummyWalker = new DummyWalker();
    private final Scope scope;
    Stack<Obj> stack = new Stack<>();
    private long timeout = System.currentTimeMillis() + GlobalVisitorSettings.MILLISECONDS_LIMIT;

    public StackMachineWalker(Scope scope) {
        this.scope = scope;
    }

    public StackMachineWalker(StackMachineWalker parent, Scope scope) {
        this.timeout = parent.timeout;
        this.scope = scope;
    }

    @Override
    public void opcodeReturn() throws IOException {
        checkTimeout();
        throw new ReturnException(stack.pop());
    }

    @Override
    public void opcodeUndefinedConstant() throws IOException {
        checkTimeout();
        stack.push(Null.VALUE);
    }

    @Override
    public void opcodeBooleanConstantTrue() throws IOException {
        checkTimeout();
        stack.push(Bool.TRUE);
    }

    @Override
    public void opcodeBooleanConstantFalse() throws IOException {
        checkTimeout();
        stack.push(Bool.FALSE);
    }

    @Override
    public void opcodeIntConstant(DataInput input) throws IOException {
        checkTimeout();
        stack.push(Int.of(input.readInt()));
    }

    @Override
    public void opcodeDecimalConstant(DataInput input) throws IOException {
        checkTimeout();
        stack.push(Number.of(input.readDouble()));
    }

    @Override
    public void opcodeStringConstant(DataInput input, List<String> stringPool) throws IOException {
        checkTimeout();
        stack.push(Str.of(stringPool.get(input.readUnsignedShort())));
    }

    @Override
    public void opcodeNewArray() throws IOException {
        checkTimeout();
        stack.push(new Array());
    }

    @Override
    public void opcodeNewDictionary() throws IOException {
        checkTimeout();
        stack.push(new Dictionary());
    }

    @Override
    public void opcodeNewRange() throws IOException {
        checkTimeout();
        Obj endObj = stack.pop();
        Obj startObj = stack.pop();

        if (startObj instanceof Int && endObj instanceof Int) {
            int start = ((Int) startObj).value();
            int end = ((Int) endObj).value();

            GlobalVisitorSettings.checkSizeLimit(Math.abs(end - start));

            stack.push(new Range(start, end));
        } else {
            stack.push(Null.VALUE);
        }
    }

    @Override
    public void opcodeNewFunction(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        checkTimeout();
        String name = stringPool.get(input.readUnsignedShort());

        FunctionParamWalker paramWalker = new FunctionParamWalker(this);
        reader.walkInsts(input, paramWalker, stringPool, depth + 1);
        checkTimeout();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        reader.walkInsts(input, new BufferWalker(new DataOutputStream(buffer)), stringPool, depth + 1);
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

    @Override
    public void opcodeNewModule(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        checkTimeout();
        String name = stringPool.get(input.readUnsignedShort());

        Scope moduleScope = scope.subPool();
        reader.walkInsts(input, new StackMachineWalker(this, moduleScope), stringPool, depth + 1);
        checkTimeout();

        Module module = new CompiledModule(name, moduleScope);
        scope.declare(name, module);

        stack.push(module);
    }

    @Override
    public void opcodeNewType(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        checkTimeout();
        String name = stringPool.get(input.readUnsignedShort());

        Scope typeScope = scope.subPool();

        //Block 1: Parameters
        FunctionParamWalker paramWalker = new FunctionParamWalker(this);
        reader.walkInsts(input, paramWalker, stringPool, depth + 1);
        checkTimeout();

        //Block 2: SuperType
        reader.walkInsts(input, this, stringPool, depth + 1);
        checkTimeout();
        Obj superType = stack.pop();

        if (superType != Obj.TYPE && !(superType instanceof CompiledType)) {
            throw new ComputeException(superType + " can not be extended");
        }

        //Block 3: SuperParameters
        int size = stack.size();
        reader.walkInsts(input, this, stringPool, depth + 1);
        checkTimeout();

        LinkedList<Obj> superParams = new LinkedList<>();
        for (int i = stack.size(); i > size; i--) {
            superParams.push(stack.pop());
        }

        //Block 4: Inner
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        reader.walkInsts(input, new BufferWalker(new DataOutputStream(buffer)), stringPool, depth + 1);
        checkTimeout();

        //Building
        CompiledConstructor constructor = new CompiledConstructor(
                paramWalker.parameters, superParams, new CompiledExecution(buffer.toByteArray(), reader, stringPool, depth + 1, this), typeScope
        );

        CompiledType type = new CompiledType((Type) superType, name, constructor);

        scope.declare(name, type);

        stack.push(type);
    }

    @Override
    public void opcodeInvoke(DataInput input) throws IOException {
        checkTimeout();
        byte pCount = input.readByte();

        LinkedList<Obj> parameters = new LinkedList<>();
        for (byte i = 0; i < pCount; i++) {
            parameters.push(stack.pop());
        }

        Obj left = stack.pop();

        stack.push(left.invoke(parameters));
    }

    @Override
    public void opcodeDeclare(DataInput input, List<String> stringPool) throws IOException {
        checkTimeout();
        String name = stringPool.get(input.readUnsignedShort());

        Obj value = stack.pop();
        scope.declare(name, value);
        stack.push(Null.VALUE);
    }

    @Override
    public void opcodeUnaryOperation(DataInput input) throws IOException {
        checkTimeout();
        byte type = input.readByte();

        UnaryOperatorType[] values = UnaryOperatorType.values();
        if (type >= values.length) {
            throw new InvalidBytecodeException("Invalid Unary Operation type 0x" + toHex(new byte[]{type}));
        }

        Obj operand = stack.pop();

        switch (values[type]) {
            case PLUS:
                stack.push(operand);
                break;
            case MINUS:
                stack.push(operand.negative());
                break;
            case NEGATE:
                stack.push(operand.negate());
                break;
        }
    }

    @Override
    public void opcodeBinaryOperation(DataInput input) throws IOException {
        checkTimeout();
        byte type = input.readByte();

        BinaryOperatorType[] values = BinaryOperatorType.values();
        if (type >= values.length) {
            throw new InvalidBytecodeException("Invalid Binary Operation type 0x" + toHex(new byte[]{type}));
        }

        Obj right = stack.pop();
        Obj left = stack.pop();

        if (left instanceof Int && right instanceof Number) {
            left = Number.of(((Int) left).value());
        } else if (left instanceof Number && right instanceof Int) {
            right = Number.of(((Int) right).value());
        }

        switch (values[type]) {
            case PLUS:
                stack.push(left.plus(right));
                break;
            case MINUS:
                stack.push(left.minus(right));
                break;
            case TIMES:
                stack.push(left.times(right));
                break;
            case DIVIDE:
                stack.push(left.divide(right));
                break;
            case MODULUS:
                stack.push(left.mod(right));
                break;
            case POWER:
                stack.push(left.pow(right));
                break;

            case EQUALS:
                stack.push(left.isEqualTo(right));
                break;
            case NOT_EQUALS:
                stack.push(left.isEqualTo(right).negate());
                break;
            case GREATER_THAN:
                stack.push(left.greaterThan(right));
                break;
            case LESS_THAN:
                stack.push(left.lessThan(right));
                break;
            case GREATER_THAN_EQUAL:
                stack.push(left.greaterThan(right).or(left.isEqualTo(right)));
                break;
            case LESS_THAN_EQUAL:
                stack.push(left.lessThan(right).or(left.isEqualTo(right)));
                break;

            case OR:
                stack.push(left.or(right));
                break;
            case AND:
                stack.push(left.and(right));
                break;

            case SHL:
                stack.push(left.shl(right));
                break;
            case SHR:
                stack.push(left.shr(right));
                break;
        }
    }

    @Override
    public void opcodeSliceOperation() throws IOException {
        checkTimeout();
        Obj step = stack.pop();
        Obj end = stack.pop();
        Obj start = stack.pop();
        Obj left = stack.pop();

        stack.push(left.slice(start, end, step));
    }

    @Override
    public void opcodeGet() throws IOException {
        checkTimeout();
        Obj key = stack.pop();
        Obj left = stack.pop();

        stack.push(left.get(key));
    }

    @Override
    public void opcodeSet() throws IOException {
        checkTimeout();
        Obj value = stack.pop();
        Obj key = stack.pop();
        Obj left = stack.pop();

        stack.push(left.set(key, value));
    }

    @Override
    public void opcodeIdentifier(DataInput input, List<String> stringPool) throws IOException {
        checkTimeout();
        boolean parented = input.readBoolean();
        String name = stringPool.get(input.readUnsignedShort());

        if (parented) {
            stack.push(stack.pop().getAttr(name));
        } else {
            if (!scope.contains(name)) {
                throw new ComputeException(name + " is not defined");
            }

            stack.push(scope.get(name));
        }
    }

    @Override
    public void opcodeAssign(DataInput input, List<String> stringPool) throws IOException {
        checkTimeout();
        boolean parented = input.readBoolean();
        String name = stringPool.get(input.readUnsignedShort());

        Obj value = stack.pop();

        if (parented) {
            stack.push(stack.pop().setAttr(name, value));
        } else {
            if (!scope.contains(name)) {
                throw new ComputeException(name + " is not defined");
            }

            scope.assign(name, value);
            stack.push(value);
        }
    }

    @Override
    public void opcodeConditional(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        checkTimeout();
        boolean hasElseBranch = input.readBoolean();
        reader.walkInsts(input, this, stringPool, depth + 1);
        checkTimeout();

        if (stack.pop() == Bool.TRUE) {
            reader.walkInsts(input, this, stringPool, depth + 1);
            checkTimeout();
            if (hasElseBranch) reader.walkInsts(input, dummyWalker, stringPool, depth + 1);
        } else {
            reader.walkInsts(input, dummyWalker, stringPool, depth + 1);
            checkTimeout();
            if (hasElseBranch) reader.walkInsts(input, this, stringPool, depth + 1);
            else stack.push(Null.VALUE);
        }
    }

    @Override
    public void opcodeForEach(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        checkTimeout();
        String variant = stringPool.get(input.readUnsignedShort());

        reader.walkInsts(input, this, stringPool, depth + 1);
        checkTimeout();
        Obj iterObj = stack.pop();

        if (!(iterObj instanceof Iterable)) {
            stack.push(Null.VALUE);
            return;
        }

        Iterable iterable = (Iterable) iterObj;

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        reader.walkInsts(input, new BufferWalker(new DataOutputStream(buffer)), stringPool, depth + 1);
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
            reader.walkInsts(new DataInputStream(new ByteArrayInputStream(bytecode)), new StackMachineWalker(this, copy), stringPool, depth + 1);
            checkTimeout();
            iter++;
        }

        stack.push(Null.VALUE);
    }

    @Override
    public void opcodeDup() throws IOException {
        checkTimeout();
        stack.push(stack.peek());
    }

    @Override
    public void opcodePop() throws IOException {
        checkTimeout();
        stack.pop();
    }

    public Stack<Obj> getStack() {
        return stack;
    }

    public Scope getScope() {
        return scope;
    }

    public void resetTimeout() {
        timeout = System.currentTimeMillis() + GlobalVisitorSettings.MILLISECONDS_LIMIT;
    }

    void checkTimeout() {
        GlobalVisitorSettings.checkTimeout(timeout);
    }
}
