package xyz.avarel.kaiper.vm;

import xyz.avarel.kaiper.bytecode.reader.walker.BufferWalker;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.bytecode.reader.walker.BytecodeWalkerAdapter;
import xyz.avarel.kaiper.bytecode.reader.walker.DummyWalker;
import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.exceptions.InvalidBytecodeException;
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

    @Override
    public boolean opcodeReturn() throws IOException {
        checkTimeout();
        return false;
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
    public void opcodeIntConstant(DataInput in) throws IOException {
        checkTimeout();
        stack.push(Int.of(in.readInt()));
    }

    @Override
    public void opcodeDecimalConstant(DataInput in) throws IOException {
        checkTimeout();
        stack.push(Number.of(in.readDouble()));
    }

    @Override
    public void opcodeStringConstant(DataInput in, List<String> stringPool) throws IOException {
        checkTimeout();
        stack.push(Str.of(stringPool.get(in.readUnsignedShort())));
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

    @Override
    public void opcodeNewModule(DataInput in, OpcodeReader reader, List<String> stringPool, int depth) throws IOException {
        checkTimeout();
        String name = stringPool.get(in.readUnsignedShort());

        Scope moduleScope = scope.subPool();
        reader.read(in, new StackMachineWalker(this, moduleScope), stringPool, depth + 1);
        checkTimeout();

        Module module = new CompiledModule(name, moduleScope);
        scope.declare(name, module);

        stack.push(module);
    }

    @Override
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

    @Override
    public void opcodeInvoke(DataInput in) throws IOException {
        checkTimeout();
        byte pCount = in.readByte();

        LinkedList<Obj> parameters = new LinkedList<>();
        for (byte i = 0; i < pCount; i++) {
            parameters.push(stack.pop());
        }

        Obj left = stack.pop();

        stack.push(left.invoke(parameters));
    }

    @Override
    public void opcodeDeclare(DataInput in, List<String> stringPool) throws IOException {
        checkTimeout();
        String name = stringPool.get(in.readUnsignedShort());

        Obj value = stack.pop();
        scope.declare(name, value);
        stack.push(Null.VALUE);
    }

    @Override
    public void opcodeUnaryOperation(DataInput in) throws IOException {
        checkTimeout();
        byte type = in.readByte();

        UnaryOperatorType[] values = UnaryOperatorType.values();
        if (type >= values.length) {
            throw new InvalidBytecodeException("Invalid Unary Operation type 0x" + toHex(type));
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
    public void opcodeBinaryOperation(DataInput in) throws IOException {
        checkTimeout();
        byte type = in.readByte();

        BinaryOperatorType[] values = BinaryOperatorType.values();
        if (type >= values.length) {
            throw new InvalidBytecodeException("Invalid Binary Operation type 0x" + toHex(type));
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
                stack.push(Bool.of(left.compareTo(right) == 1));
                break;
            case LESS_THAN:
                stack.push(Bool.of(left.compareTo(right) == -1));
                break;
            case GREATER_THAN_EQUAL:
                stack.push(Bool.of(left.compareTo(right) >= 0));
                break;
            case LESS_THAN_EQUAL:
                stack.push(Bool.of(left.compareTo(right) <= 0));
                break;
            case OR: {
                Obj left = resultOf(expr.getLeft(), scope);
                return left == Bool.TRUE ? Bool.TRUE : resultOf(expr.getRight(), scope);
            }
            case AND: {
                Obj left = resultOf(expr.getLeft(), scope);
                return left == Bool.FALSE ? Bool.FALSE : resultOf(expr.getRight(), scope);
            }

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
    public void opcodeIdentifier(DataInput in, List<String> stringPool) throws IOException {
        checkTimeout();
        boolean parented = in.readBoolean();
        String name = stringPool.get(in.readUnsignedShort());

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
    public void opcodeAssign(DataInput in, List<String> stringPool) throws IOException {
        checkTimeout();
        boolean parented = in.readBoolean();
        String name = stringPool.get(in.readUnsignedShort());

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

    @Override
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

    @Override
    public void opcodeDup() throws IOException {
        checkTimeout();
    }

    @Override
    public void opcodePop() throws IOException {
        checkTimeout();
        stack.pop();
    }

    public void resetTimeout() {
        timeout = System.currentTimeMillis() + GlobalVisitorSettings.MILLISECONDS_LIMIT;
    }

    public void checkTimeout() {
        GlobalVisitorSettings.checkTimeout(timeout);
    }
}
