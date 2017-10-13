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

package xyz.avarel.kaiper.vm.executor;

import xyz.avarel.kaiper.bytecode.io.KDataInput;
import xyz.avarel.kaiper.bytecode.io.KDataInputStream;
import xyz.avarel.kaiper.bytecode.io.KDataOutputStream;
import xyz.avarel.kaiper.bytecode.io.NullOutputStream;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.opcodes.PatternOpcodes;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.bytecode.reader.processors.KOpcodeProcessorAdapter;
import xyz.avarel.kaiper.bytecode.reader.processors.ReadResult;
import xyz.avarel.kaiper.bytecode.reader.processors.impl.OpcodeBufferProcessor;
import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.exceptions.InvalidBytecodeException;
import xyz.avarel.kaiper.operations.BinaryOperatorType;
import xyz.avarel.kaiper.operations.UnaryOperatorType;
import xyz.avarel.kaiper.runtime.*;
import xyz.avarel.kaiper.runtime.collections.Array;
import xyz.avarel.kaiper.runtime.collections.Dictionary;
import xyz.avarel.kaiper.runtime.collections.Range;
import xyz.avarel.kaiper.runtime.functions.Func;
import xyz.avarel.kaiper.runtime.modules.CompiledModule;
import xyz.avarel.kaiper.runtime.modules.Module;
import xyz.avarel.kaiper.runtime.numbers.Int;
import xyz.avarel.kaiper.runtime.numbers.Number;
import xyz.avarel.kaiper.vm.GlobalExecutionSettings;
import xyz.avarel.kaiper.vm.compiled.CompiledExecution;
import xyz.avarel.kaiper.vm.compiled.PreparedPatternExecution;
import xyz.avarel.kaiper.vm.runtime.functions.CompiledFunc;
import xyz.avarel.kaiper.vm.runtime.types.CompiledConstructor;
import xyz.avarel.kaiper.vm.runtime.types.CompiledType;
import xyz.avarel.kaiper.vm.utils.VMStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import static xyz.avarel.kaiper.bytecode.BytecodeUtils.toHex;
import static xyz.avarel.kaiper.bytecode.reader.processors.ReadResult.*;

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
public class StackMachine extends KOpcodeProcessorAdapter {
    public static boolean assign(Scope target, String key, Obj value) {
        if (target.getMap().containsKey(key)) {
            target.put(key, value);
            return true;
        } else for (Scope parent : target.getParents()) {
            if (assign(parent, key, value)) {
                return true;
            }
        }
        return false;
    }

    public static void declare(Scope target, String key, Obj value) {
        if (target.getMap().containsKey(key)) {
            throw new ComputeException(key + " already exists in the scope");
        }
        target.put(key, value);
    }
    /**
     * The Breakpoint object (used on resumeBreakpoint and opcodeBreakpoint)
     */
    public final Object breakpointObj = new Object();
    /**
     * Recyclable Buffer (ByteArray)
     */
    public final ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
    /**
     * Recyclable Buffer (ByteOutput)
     */
    public final KDataOutputStream byteBufferOutput = new KDataOutputStream(byteBuffer);
    /**
     * Recyclable Buffer (OpcodeBufferConsumer)
     */
    public final OpcodeBufferProcessor buffer = new OpcodeBufferProcessor(NullOutputStream.DATA_INSTANCE);
    /**
     * Recyclable PatternCreator
     */
    public final PatternProcessor patternProcessor = new PatternProcessor(this);
    /**
     * String Pool (short => String)
     */
    public String[] stringPool;
    /**
     * Current Scope
     */
    public Scope scope;
    /**
     * Current Stack
     */
    public VMStack<Obj> stack = new VMStack<>();
    public long breakpointMillis = 0;
    public long timeout = System.currentTimeMillis() + GlobalExecutionSettings.MILLISECONDS_LIMIT;
    /**
     * Current Line Number
     */
    public long lineNumber = -1;

    public StackMachine(String[] stringPool, Scope scope) {
        this.stringPool = stringPool;
        this.scope = scope;
    }

    public StackMachine() {
    }

    @Override
    public ReadResult process(OpcodeReader reader, Opcode opcode, KDataInput in) {
        checkTimeout();
        ReadResult result = super.process(reader, opcode, in);
        checkTimeout();
        return result;
    }

    @Override
    public ReadResult opcodeEnd(OpcodeReader reader, KDataInput in) {
        return ENDED;
    }

    @Override
    public ReadResult opcodeLineNumber(OpcodeReader reader, KDataInput in) {
        this.lineNumber = in.readLong();
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeBreakpoint(OpcodeReader reader, KDataInput in) {
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
    public ReadResult opcodeReturn(OpcodeReader reader, KDataInput in) {
        return RETURNED;
    }

    @Override
    public ReadResult opcodeDup(OpcodeReader reader, KDataInput in) {
        stack.push(stack.peek());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodePop(OpcodeReader reader, KDataInput in) {
        stack.pop();
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNullConstant(OpcodeReader reader, KDataInput in) {
        stack.push(Null.VALUE);
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeBooleanConstant(OpcodeReader reader, boolean value, KDataInput in) {
        stack.push(Bool.of(value));
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeIntConstant(OpcodeReader reader, KDataInput in) {
        stack.push(Int.of(in.readInt()));
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeDecimalConstant(OpcodeReader reader, KDataInput in) {
        stack.push(Number.of(in.readDouble()));
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeStringConstant(OpcodeReader reader, KDataInput in) {
        stack.push(Str.of(stringPool[in.readUnsignedShort()]));
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewArray(OpcodeReader reader, KDataInput in) {
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
    public ReadResult opcodeNewDictionary(OpcodeReader reader, KDataInput in) {
        stack.push(new Dictionary());
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewRange(OpcodeReader reader, KDataInput in) {
        Obj endObj = stack.pop();
        Obj startObj = stack.pop();

        if (!(startObj instanceof Int) || !(endObj instanceof Int)) {
            stack.push(Null.VALUE);
            return CONTINUE;
        }

        int start = ((Int) startObj).value();
        int end = ((Int) endObj).value();

        GlobalExecutionSettings.checkSizeLimit(Math.abs(end - start));

        stack.push(new Range(start, end));
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewFunction(OpcodeReader reader, KDataInput in) {
        String name = stringPool[in.readUnsignedShort()];

        int patternArity = in.readInt();

        byteBuffer.reset();
        PatternOpcodes.READER.read(buffer.setOut(byteBufferOutput), in);
        byte[] patternBytecode = byteBuffer.toByteArray();

        byteBuffer.reset();
        reader.read(buffer.setOut(byteBufferOutput), in);
        byte[] function = byteBuffer.toByteArray();

        Func func = new CompiledFunc(
                name.isEmpty() ? null : name,
                new PreparedPatternExecution(patternArity, patternBytecode, stringPool),
                new CompiledExecution(function, stringPool),
                scope.subPool()
        );

        if (func.getName() != null) declare(scope, func.getName(), func);
        stack.push(func);

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewModule(OpcodeReader reader, KDataInput in) {
        String name = stringPool[in.readUnsignedShort()];
        Scope moduleScope = scope.subPool();

        //save state
        Scope lastScope = scope;
        int lastLock = stack.lock();
        scope = moduleScope;

        reader.read(this, in);

        //load state
        scope = lastScope;
        stack.popToLock();
        stack.setLock(lastLock);

        Module module = new CompiledModule(name, moduleScope);
        declare(scope, name, module);
        stack.push(module);

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewType(OpcodeReader reader, KDataInput in) {
        String name = stringPool[in.readUnsignedShort()];

        int patternArity = in.readInt();

        byteBuffer.reset();
        PatternOpcodes.READER.read(buffer.setOut(byteBufferOutput), in);
        byte[] patternBytecode = byteBuffer.toByteArray();

        byteBuffer.reset();
        reader.read(buffer.setOut(byteBufferOutput), in);
        byte[] function = byteBuffer.toByteArray();

        CompiledConstructor constructor = new CompiledConstructor(
                new PreparedPatternExecution(patternArity, patternBytecode, stringPool),
                new CompiledExecution(function, stringPool),
                scope.subPool()
        );

        CompiledType type = new CompiledType(name, constructor);
        declare(scope, name, type);
        stack.push(type);

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeNewTuple(OpcodeReader reader, KDataInput in) {
        int size = in.readInt();

        Map<String, Obj> map = new LinkedHashMap<>();

        for (int i = 0; i < size; i++) {
            String key = stringPool[in.readUnsignedShort()];
            reader.read(this, in);
            map.put(key, stack.pop());
        }

        stack.push(new Tuple(map));

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeDeclare(OpcodeReader reader, KDataInput in) {
        String name = stringPool[in.readUnsignedShort()];

        Obj value = stack.pop();
        declare(scope, name, value);
        stack.push(Null.VALUE);

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeAssign(OpcodeReader reader, KDataInput in) {
        boolean parented = in.readBoolean();
        String name = stringPool[in.readUnsignedShort()];

        Obj value = stack.pop();

        if (parented) {
            stack.push(stack.pop().setAttr(name, value));
        } else {
            if (!assign(scope, name, value)) {
                throw new ComputeException(name + " is not defined, it must be declared first");
            }

            stack.push(value);
        }

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeIdentifier(OpcodeReader reader, KDataInput in) {
        boolean parented = in.readBoolean();
        String name = stringPool[in.readUnsignedShort()];

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
    public ReadResult opcodeBindDeclare(OpcodeReader reader, KDataInput in) {
        // TODO: 11/09/2017
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeBindAssign(OpcodeReader reader, KDataInput in) {
        // TODO: 11/09/2017
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeInvoke(OpcodeReader reader, KDataInput in) {
        Obj arg = stack.pop();
        Obj left = stack.pop();

        stack.push(left.invoke((Tuple) arg)); //if this implodes, it's Avarel's fault

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeUnaryOperation(OpcodeReader reader, KDataInput in) {
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
    public ReadResult opcodeBinaryOperation(OpcodeReader reader, KDataInput in) {
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

            Bool bool = Bool.of(operator == BinaryOperatorType.OR);
            if (left == bool) {
                //Standard way of skipping a block of code.
                reader.read(buffer.setOut(NullOutputStream.DATA_INSTANCE), in);
                stack.push(bool);
            } else {
                reader.read(this, in);
                //stack.push(stack.pop()); //oh nvm
            }

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
    public ReadResult opcodeSliceOperation(OpcodeReader reader, KDataInput in) {
        Obj step = stack.pop();
        Obj end = stack.pop();
        Obj start = stack.pop();
        Obj left = stack.pop();

        stack.push(left.slice(start, end, step));

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeArrayGet(OpcodeReader reader, KDataInput in) {
        Obj key = stack.pop();
        Obj left = stack.pop();

        stack.push(left.get(key));

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeArraySet(OpcodeReader reader, KDataInput in) {
        Obj value = stack.pop();
        Obj key = stack.pop();
        Obj left = stack.pop();

        stack.push(left.set(key, value));

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeConditional(OpcodeReader reader, KDataInput in) {
        boolean hasElseBranch = in.readBoolean();

        reader.read(this, in);
        checkTimeout();

        if (stack.pop() == Bool.TRUE) {
            reader.read(this, in);
            checkTimeout();

            if (hasElseBranch) {
                reader.read(buffer.setOut(NullOutputStream.DATA_INSTANCE), in);
            }
        } else {
            reader.read(buffer.setOut(NullOutputStream.DATA_INSTANCE), in);
            checkTimeout();

            if (hasElseBranch) {
                reader.read(this, in);
            } else {
                stack.push(Null.VALUE);
            }
        }

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeForEach(OpcodeReader reader, KDataInput in) {
        String variant = stringPool[in.readUnsignedShort()];

        reader.read(this, in);
        checkTimeout();

        Obj iterObj = stack.pop();


        if (!(iterObj instanceof Iterable)) {
            reader.read(buffer.setOut(NullOutputStream.DATA_INSTANCE), in);

            stack.push(Null.VALUE);

            return CONTINUE;
        }

        byteBuffer.reset();
        reader.read(buffer.setOut(byteBufferOutput), in);
        ByteArrayInputStream buffer = new ByteArrayInputStream(byteBuffer.toByteArray());

        KDataInput bufferInput = new KDataInputStream(buffer);

        //save state
        Scope lastScope = scope;
        int lastLock = stack.lock();

        int iteration = 0;
        for (Object var : (Iterable) iterObj) {
            checkTimeout();
            GlobalExecutionSettings.checkIterationLimit(iteration);

            if (!(var instanceof Obj)) {
                throw new ComputeException("Items in iterable do not implement Obj interface");
            }

            scope = lastScope.subPool();
            declare(scope, variant, (Obj) var);

            reader.read(this, bufferInput);
            checkTimeout();
            buffer.reset();
            iteration++;
        }

        //load state
        scope = lastScope;
        stack.popToLock();
        stack.setLock(lastLock);

        stack.push(Null.VALUE);

        return CONTINUE;
    }

    @Override
    public ReadResult opcodeWhile(OpcodeReader reader, KDataInput in) {

        byteBuffer.reset();
        buffer.setOut(byteBufferOutput);
        reader.read(buffer, in);
        ByteArrayInputStream bufferCondition = new ByteArrayInputStream(byteBuffer.toByteArray());
        byteBuffer.reset();
        reader.read(buffer, in);
        ByteArrayInputStream bufferAction = new ByteArrayInputStream(byteBuffer.toByteArray());

        KDataInput conditionInput = new KDataInputStream(bufferCondition);
        KDataInput actionInput = new KDataInputStream(bufferAction);

        //save state
        Scope lastScope = scope;
        int lastLock = stack.lock();

        int iteration = 0;

        while (true) {
            GlobalExecutionSettings.checkIterationLimit(iteration);

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

        stack.push(Null.VALUE);

        return CONTINUE;
    }

    public void resetTimeout() {
        timeout = System.currentTimeMillis() + GlobalExecutionSettings.MILLISECONDS_LIMIT;
    }

    public void checkTimeout() {
        GlobalExecutionSettings.checkTimeout(timeout);
    }

    public void resumeBreakpoint() {
        synchronized (breakpointObj) {
            timeout += System.currentTimeMillis() - breakpointMillis;
            breakpointObj.notify();
        }
    }
}
