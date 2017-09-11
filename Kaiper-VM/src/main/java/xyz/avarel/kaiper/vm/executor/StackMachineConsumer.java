package xyz.avarel.kaiper.vm.executor;

import xyz.avarel.kaiper.bytecode.io.ByteInput;
import xyz.avarel.kaiper.bytecode.io.ByteInputStream;
import xyz.avarel.kaiper.bytecode.io.ByteOutputStream;
import xyz.avarel.kaiper.bytecode.io.NullOutputStream;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.bytecode.reader.consumers.OpcodeConsumerAdapter;
import xyz.avarel.kaiper.bytecode.reader.consumers.ReadResult;
import xyz.avarel.kaiper.bytecode.reader.consumers.impl.OpcodeBufferConsumer;
import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.exceptions.InvalidBytecodeException;
import xyz.avarel.kaiper.operations.BinaryOperatorType;
import xyz.avarel.kaiper.operations.UnaryOperatorType;
import xyz.avarel.kaiper.runtime.*;
import xyz.avarel.kaiper.runtime.collections.Array;
import xyz.avarel.kaiper.runtime.collections.Dictionary;
import xyz.avarel.kaiper.runtime.collections.Range;
import xyz.avarel.kaiper.runtime.modules.CompiledModule;
import xyz.avarel.kaiper.runtime.modules.Module;
import xyz.avarel.kaiper.runtime.numbers.Int;
import xyz.avarel.kaiper.runtime.numbers.Number;
import xyz.avarel.kaiper.scope.Scope;
import xyz.avarel.kaiper.vm.GlobalVisitorSettings;
import xyz.avarel.kaiper.vm.utils.VMStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import static xyz.avarel.kaiper.bytecode.BytecodeUtils.toHex;
import static xyz.avarel.kaiper.bytecode.reader.consumers.ReadResult.*;

/**
 * <p>
 * The main (internal) class of the KaiperVM.
 * </p>
 * <p><em>
 * Beware that every instance of this class <b>ARE NOT</b> thread-safe, unless when halted by breakpoints. The same can be assumed from most fields.
 * To reduce RAM and CPU costs, some objects (specially IO) are recycled, as thread-safety is not a concern.
 * </em></p>
 * <p><em>
 * Each instance may be used to "execute" <b>one, and only one</b> chunk of bytecode <b>at the same time</b>.
 * </em></p>
 *
 * @author AdrianTodt
 */
public class StackMachineConsumer extends OpcodeConsumerAdapter {
    /**
     * The Breakpoint object (used on resumeBreakpoint and opcodeBreakpoint)
     */
    public final Object breakpointObj = new Object();
    /**
     * String Pool (short => String)
     */
    public final List<String> stringPool;

    /**
     * Recyclable Buffer (ByteArray)
     */
    private final ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
    /**
     * Recyclable Buffer (ByteOutput)
     */
    private final ByteOutputStream byteBufferOutput = new ByteOutputStream(byteBuffer);
    /**
     * Recyclable Buffer (OpcodeBufferConsumer)
     */
    private final OpcodeBufferConsumer buffer = new OpcodeBufferConsumer(NullOutputStream.DATA_INSTANCE);

    /**
     * Current Scope
     */
    public Scope scope;
    /**
     * Current Stack
     */
    public VMStack<Obj> stack = new VMStack<>();
    public long breakpointMillis = 0;
    public long timeout = System.currentTimeMillis() + GlobalVisitorSettings.MILLISECONDS_LIMIT;
    /**
     * Current Depth
     */
    public int depth = 0;
    /**
     * Current Line Number
     */
    public long lineNumber = -1;

    public StackMachineConsumer(Scope scope, List<String> stringPool) {
        this.stringPool = stringPool;
        this.scope = scope;
    }

    @Override
    public ReadResult accept(OpcodeReader reader, Opcode opcode, ByteInput in) {
        checkTimeout();
        ReadResult result = super.accept(reader, opcode, in);
        checkTimeout();
        return result;
    }

    @Override
    public ReadResult opcodeEnd(OpcodeReader reader, ByteInput in) {
        short endId = in.readShort();

        if (endId != depth - 1) {
            throw new InvalidBytecodeException("Unexpected End " + endId);
        }

        return ENDED;
    }

    @Override
    public ReadResult opcodeLineNumber(OpcodeReader reader, ByteInput in) {
        this.lineNumber = in.readLong();
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeBreakpoint(OpcodeReader reader, ByteInput in) {
        synchronized (breakpointObj) {
            try {
                breakpointObj.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeReturn(OpcodeReader reader, ByteInput in) {
        return RETURNED;
    }

    @Override
    public ReadResult opcodeDup(OpcodeReader reader, ByteInput in) {
        stack.push(stack.peek());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodePop(OpcodeReader reader, ByteInput in) {
        stack.pop();
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNullConstant(OpcodeReader reader, ByteInput in) {
        stack.push(Null.VALUE);
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeBooleanConstant(OpcodeReader reader, boolean value, ByteInput in) {
        stack.push(Bool.of(value));
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeIntConstant(OpcodeReader reader, ByteInput in) {
        stack.push(Int.of(in.readInt()));
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeDecimalConstant(OpcodeReader reader, ByteInput in) {
        stack.push(Number.of(in.readDouble()));
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeStringConstant(OpcodeReader reader, ByteInput in) {
        stack.push(Str.of(stringPool.get(in.readUnsignedShort())));
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewArray(OpcodeReader reader, ByteInput in) {
        int size = in.readInt();
        Array array = new Array();

        if (size != 0) {
            int lastLock = stack.setRelativeLock(size);

            while (stack.canPop()) {
                array.add(0, stack.pop());
            }

            stack.setLock(lastLock);
        }

        stack.push(array);
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewDictionary(OpcodeReader reader, ByteInput in) {
        stack.push(new Dictionary());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewRange(OpcodeReader reader, ByteInput in) {
        Obj endObj = stack.pop();
        Obj startObj = stack.pop();

        if (!(startObj instanceof Int) || !(endObj instanceof Int)) {
            stack.push(Null.VALUE);
            return CONTINUE;
        }

        int start = ((Int) startObj).value();
        int end = ((Int) endObj).value();

        GlobalVisitorSettings.checkSizeLimit(Math.abs(end - start));

        stack.push(new Range(start, end));
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewFunction(OpcodeReader reader, ByteInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewModule(OpcodeReader reader, ByteInput in) {
        String name = stringPool.get(in.readUnsignedShort());
        Scope moduleScope = scope.subPool();

        depth++;

        //save state
        Scope lastScope = scope;
        int lastLock = stack.lock();
        scope = moduleScope;

        reader.read(this, in);

        //load state
        scope = lastScope;
        stack.popToLock();
        stack.setLock(lastLock);

        depth--;

        Module module = new CompiledModule(name, moduleScope);
        scope.declare(name, module);
        stack.push(module);

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewType(OpcodeReader reader, ByteInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewTuple(OpcodeReader reader, ByteInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeDeclare(OpcodeReader reader, ByteInput in) {
        String name = stringPool.get(in.readUnsignedShort());

        Obj value = stack.pop();
        scope.declare(name, value);
        stack.push(Null.VALUE);

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeAssign(OpcodeReader reader, ByteInput in) {
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

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeIdentifier(OpcodeReader reader, ByteInput in) {
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

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeBindDeclare(OpcodeReader reader, ByteInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeBindAssign(OpcodeReader reader, ByteInput in) {
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeInvoke(OpcodeReader reader, ByteInput in) {
        Obj arg = stack.pop();
        Obj left = stack.pop();

        stack.push(left.invoke((Tuple) arg)); //if this implodes, it's Avarel's fault
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeUnaryOperation(OpcodeReader reader, ByteInput in) {
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

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeBinaryOperation(OpcodeReader reader, ByteInput in) {
        byte type = in.readByte();

        BinaryOperatorType[] values = BinaryOperatorType.values();
        if (type >= values.length) {
            throw new InvalidBytecodeException("Invalid Binary Operation type 0x" + toHex(type));
        }

        BinaryOperatorType operator = values[type];

        //Special Handling
        if (operator == BinaryOperatorType.OR || operator == BinaryOperatorType.AND) {
            Obj left = stack.pop();

            /*
            There's some true black magic programming down here. Let me explain it:
            Originally, this was a if (operator==OR) { ... } else if (operator==AND) { ... } block.
            But the only difference was the comparison (left == bool) and the stack push (stack.push(bool);).
            By comparing the operator once more we can eliminate any need for "duplicated" code.

            (Sorry if my english is shitty right now, it's 10 PM. - Adrian)
             */

            depth++;
            Bool bool = Bool.of(operator == BinaryOperatorType.OR);
            if (left == bool) {
                //Standard way of skipping a block of code.
                reader.read(buffer.reset(NullOutputStream.DATA_INSTANCE, depth), in);
                stack.push(bool);
            } else {
                reader.read(this, in);
                //stack.push(stack.pop()); //oh nvm
            }
            depth--;
            return CONTINUE;
        }

        //Normal Handling

        Obj right = stack.pop();
        Obj left = stack.pop();

        if (left instanceof Int && right instanceof Number) {
            left = Number.of(((Int) left).value());
        } else if (left instanceof Number && right instanceof Int) {
            right = Number.of(((Int) right).value());
        }

        switch (operator) {
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

            case SHL:
                stack.push(left.shl(right));
                break;
            case SHR:
                stack.push(left.shr(right));
                break;
        }

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeSliceOperation(OpcodeReader reader, ByteInput in) {
        Obj step = stack.pop();
        Obj end = stack.pop();
        Obj start = stack.pop();
        Obj left = stack.pop();

        stack.push(left.slice(start, end, step));

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeArrayGet(OpcodeReader reader, ByteInput in) {
        Obj key = stack.pop();
        Obj left = stack.pop();

        stack.push(left.get(key));

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeArraySet(OpcodeReader reader, ByteInput in) {
        Obj value = stack.pop();
        Obj key = stack.pop();
        Obj left = stack.pop();

        stack.push(left.set(key, value));

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeConditional(OpcodeReader reader, ByteInput in) {
        boolean hasElseBranch = in.readBoolean();

        depth++;
        reader.read(this, in);
        checkTimeout();

        if (stack.pop() == Bool.TRUE) {
            reader.read(this, in);
            checkTimeout();

            if (hasElseBranch) {
                reader.read(buffer.reset(NullOutputStream.DATA_INSTANCE, depth), in);
            }
        } else {
            reader.read(buffer.reset(NullOutputStream.DATA_INSTANCE, depth), in);
            checkTimeout();

            if (hasElseBranch) {
                reader.read(this, in);
            } else {
                stack.push(Null.VALUE);
            }
        }

        depth--;

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeForEach(OpcodeReader reader, ByteInput in) {
        String variant = stringPool.get(in.readUnsignedShort());

        depth++;
        reader.read(this, in);
        checkTimeout();

        Obj iterObj = stack.pop();


        if (!(iterObj instanceof Iterable)) {
            reader.read(buffer.reset(NullOutputStream.DATA_INSTANCE, depth), in);

            stack.push(Null.VALUE);
            return CONTINUE;
        }

        byteBuffer.reset();
        reader.read(buffer.reset(byteBufferOutput, depth), in);
        ByteArrayInputStream buffer = new ByteArrayInputStream(byteBuffer.toByteArray());

        ByteInput bufferInput = new ByteInputStream(buffer);

        //save state
        Scope lastScope = scope;
        int lastLock = stack.lock();

        int iteration = 0;
        for (Object var : (Iterable) iterObj) {
            checkTimeout();
            GlobalVisitorSettings.checkIterationLimit(iteration);

            if (!(var instanceof Obj)) {
                throw new ComputeException("Items in iterable do not implement Obj interface");
            }

            scope = lastScope.subPool();
            scope.declare(variant, (Obj) var);

            reader.read(this, bufferInput);
            checkTimeout();
            buffer.reset();
            iteration++;
        }

        //load state
        scope = lastScope;
        stack.popToLock();
        stack.setLock(lastLock);

        depth--;

        stack.push(Null.VALUE);
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeWhile(OpcodeReader reader, ByteInput in) {
        depth++;

        byteBuffer.reset();
        buffer.reset(byteBufferOutput, depth);
        reader.read(buffer, in);
        ByteArrayInputStream bufferCondition = new ByteArrayInputStream(byteBuffer.toByteArray());
        byteBuffer.reset();
        reader.read(buffer, in);
        ByteArrayInputStream bufferAction = new ByteArrayInputStream(byteBuffer.toByteArray());

        ByteInput conditionInput = new ByteInputStream(bufferCondition);
        ByteInput actionInput = new ByteInputStream(bufferAction);

        //save state
        Scope lastScope = scope;
        int lastLock = stack.lock();

        int iteration = 0;

        while (true) {
            GlobalVisitorSettings.checkIterationLimit(iteration);

            scope = lastScope.subPool();
            reader.read(this, conditionInput);
            checkTimeout();
            Obj condition = stack.pop();
            stack.popToLock(); //reset stack

            if (condition instanceof Bool && condition == Bool.TRUE) {
                scope = lastScope.subPool();
                reader.read(this, actionInput);
                checkTimeout();
                stack.popToLock(); //reset stack
            } else {
                break;
            }

            iteration++;
        }

        //load state
        scope = lastScope;
        stack.popToLock();
        stack.setLock(lastLock);

        depth--;

        stack.push(Null.VALUE);
        return CONTINUE;
    }

    public void resetTimeout() {
        timeout = System.currentTimeMillis() + GlobalVisitorSettings.MILLISECONDS_LIMIT;
    }

    public void checkTimeout() {
        GlobalVisitorSettings.checkTimeout(timeout);
    }

    public void resumeBreakpoint() {
        synchronized (breakpointObj) {
            timeout += System.currentTimeMillis() - breakpointMillis;
            breakpointObj.notify();
        }
    }
}
