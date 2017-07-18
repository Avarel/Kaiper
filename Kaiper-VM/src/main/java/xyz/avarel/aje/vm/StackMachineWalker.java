package xyz.avarel.aje.vm;

import xyz.avarel.aje.bytecode.walker.BufferWalker;
import xyz.avarel.aje.bytecode.walker.BytecodeBatchReader;
import xyz.avarel.aje.bytecode.walker.BytecodeWalkerAdapter;
import xyz.avarel.aje.bytecode.walker.DummyWalker;
import xyz.avarel.aje.exceptions.ComputeException;
import xyz.avarel.aje.exceptions.InvalidBytecodeException;
import xyz.avarel.aje.exceptions.ReturnException;
import xyz.avarel.aje.operations.BinaryOperatorType;
import xyz.avarel.aje.operations.UnaryOperatorType;
import xyz.avarel.aje.runtime.Bool;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Str;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.collections.Array;
import xyz.avarel.aje.runtime.collections.Dictionary;
import xyz.avarel.aje.runtime.collections.Range;
import xyz.avarel.aje.runtime.numbers.Int;
import xyz.avarel.aje.runtime.numbers.Number;
import xyz.avarel.aje.scope.Scope;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import static xyz.avarel.aje.bytecode.BytecodeUtils.toHex;

class StackMachineWalker extends BytecodeWalkerAdapter {
    private static final DummyWalker dummyWalker = new DummyWalker();

    private final Scope scope;

    Stack<Obj> stack = new Stack<>();

    StackMachineWalker(Scope scope) {
        this.scope = scope;
    }

    @Override
    public void opcodeReturn() throws IOException {
        throw new ReturnException(stack.pop());
    }

    @Override
    public void opcodeUndefinedConstant() throws IOException {
        stack.push(Undefined.VALUE);
    }

    @Override
    public void opcodeBooleanConstantTrue() throws IOException {
        stack.push(Bool.TRUE);
    }

    @Override
    public void opcodeBooleanConstantFalse() throws IOException {
        stack.push(Bool.FALSE);
    }

    @Override
    public void opcodeIntConstant(DataInput input) throws IOException {
        stack.push(Int.of(input.readInt()));
    }

    @Override
    public void opcodeDecimalConstant(DataInput input) throws IOException {
        stack.push(Number.of(input.readDouble()));
    }

    @Override
    public void opcodeStringConstant(DataInput input, List<String> stringPool) throws IOException {
        stack.push(Str.of(stringPool.get(input.readUnsignedShort())));
    }

    @Override
    public void opcodeNewArray() throws IOException {
        stack.push(new Array());
    }

    @Override
    public void opcodeNewDictionary() throws IOException {
        stack.push(new Dictionary());
    }

    @Override
    public void opcodeNewRange() throws IOException {
        Obj endObj = stack.pop();
        Obj startObj = stack.pop();

        if (startObj instanceof Int && endObj instanceof Int) {
            int start = ((Int) startObj).value();
            int end = ((Int) endObj).value();

            stack.push(new Range(start, end));
        } else {
            stack.push(Undefined.VALUE);
        }
    }

    @Override
    public void opcodeNewFunction(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        String name = stringPool.get(input.readUnsignedShort());

        FunctionParamWalker paramWalker = new FunctionParamWalker(this);
        reader.walkInsts(input, paramWalker, stringPool, depth + 1);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        reader.walkInsts(input, new BufferWalker(new DataOutputStream(buffer)), stringPool, depth + 1);

        stack.push(new CompiledFunc(name.isEmpty() ? null : name, paramWalker.parameters, buffer.toByteArray(), scope.subPool(), reader, stringPool, depth + 1));
    }

    @Override
    public void opcodeInvoke(DataInput input) throws IOException {
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
        String name = stringPool.get(input.readUnsignedShort());

        Obj value = stack.pop();
        scope.declare(name, value);
        stack.push(Undefined.VALUE);
    }

    @Override
    public void opcodeUnaryOperation(DataInput input) throws IOException {
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
        Obj step = stack.pop();
        Obj end = stack.pop();
        Obj start = stack.pop();
        Obj left = stack.pop();

        stack.push(left.slice(start, end, step));
    }

    @Override
    public void opcodeGet() throws IOException {
        Obj key = stack.pop();
        Obj left = stack.pop();

        stack.push(left.get(key));
    }

    @Override
    public void opcodeSet() throws IOException {
        Obj value = stack.pop();
        Obj key = stack.pop();
        Obj left = stack.pop();

        stack.push(left.set(key, value));
    }

    @Override
    public void opcodeIdentifier(DataInput input, List<String> stringPool) throws IOException {
        boolean parented = input.readBoolean();
        String name = stringPool.get(input.readUnsignedShort());

        if (parented) {
            stack.push(stack.pop().getAttr(name));
        } else {
            if (!scope.contains(name)) {
                throw new ComputeException(name + " is not defined");
            }

            stack.push(scope.lookup(name));
        }
    }

    @Override
    public void opcodeAssign(DataInput input, List<String> stringPool) throws IOException {
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
        boolean hasElseBranch = input.readBoolean();
        reader.walkInsts(input, this, stringPool, depth + 1);

        if (stack.pop() == Bool.TRUE) {
            reader.walkInsts(input, this, stringPool, depth + 1);
            if (hasElseBranch) reader.walkInsts(input, dummyWalker, stringPool, depth + 1);
        } else {
            reader.walkInsts(input, dummyWalker, stringPool, depth + 1);
            if (hasElseBranch) reader.walkInsts(input, this, stringPool, depth + 1);
            else stack.push(Undefined.VALUE);
        }
    }

    @Override
    public void opcodeForEach(DataInput input, BytecodeBatchReader reader, List<String> stringPool, int depth) throws IOException {
        String variant = stringPool.get(input.readUnsignedShort());

        reader.walkInsts(input, this, stringPool, depth + 1);
        Obj iterObj = stack.pop();

        if (!(iterObj instanceof Iterable)) {
            stack.push(Undefined.VALUE);
            return;
        }

        Iterable iterable = (Iterable) iterObj;

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        reader.walkInsts(input, new BufferWalker(new DataOutputStream(buffer)), stringPool, depth + 1);
        byte[] bytecode = buffer.toByteArray();

        for (Object var : iterable) {
            if (!(var instanceof Obj)) {
                throw new ComputeException("Items in iterable do not implement Obj interface");
            }

            Scope copy = scope.subPool();
            copy.declare(variant, (Obj) var);
            reader.walkInsts(new DataInputStream(new ByteArrayInputStream(bytecode)), new StackMachineWalker(copy), stringPool, depth + 1);
        }

        stack.push(Undefined.VALUE);
    }

    @Override
    public void opcodeDup() throws IOException {
        stack.push(stack.peek());
    }

    @Override
    public void opcodePop() throws IOException {
        stack.pop();
    }
}
