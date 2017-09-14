package xyz.avarel.kaiper.vm.executor;

import xyz.avarel.kaiper.bytecode.io.KDataInput;
import xyz.avarel.kaiper.bytecode.io.NullOutputStream;
import xyz.avarel.kaiper.bytecode.opcodes.KOpcodes;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.bytecode.reader.processors.PatternOpcodeProcessorAdapter;
import xyz.avarel.kaiper.bytecode.reader.processors.ReadResult;
import xyz.avarel.kaiper.exceptions.InvalidBytecodeException;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Tuple;
import xyz.avarel.kaiper.runtime.collections.Array;

import java.util.LinkedHashMap;
import java.util.Map;

import static xyz.avarel.kaiper.bytecode.opcodes.PatternOpcodes.PATTERN_CASE;
import static xyz.avarel.kaiper.bytecode.opcodes.PatternOpcodes.READER;
import static xyz.avarel.kaiper.bytecode.reader.processors.ReadResult.*;

public class PatternProcessor extends PatternOpcodeProcessorAdapter {
    public StackMachine parent;
    public Tuple obj;
    public Map<String, Obj> results;
    public int size, position, current;

    public PatternProcessor(StackMachine parent) {
        this.parent = parent;
    }

    @Override
    public ReadResult process(OpcodeReader reader, Opcode opcode, KDataInput in) {
        parent.checkTimeout();
        ReadResult result = super.process(reader, opcode, in);
        parent.checkTimeout();
        return result;
    }

    @Override
    public ReadResult opcodeEnd(OpcodeReader reader, KDataInput in) {
        return ENDED;
    }

    @Override
    public ReadResult opcodeBreakpoint(OpcodeReader reader, KDataInput in) {
        return parent.opcodeBreakpoint(reader, in);
    }

    @Override
    public ReadResult opcodePatternCase(OpcodeReader reader, KDataInput in) {
        if (!obj.hasAttr("_" + position)) {
            return ERRORED;
        }

        Obj value = obj.getAttr("_" + position);
        position++;

        Tuple tuple = value instanceof Tuple ? (Tuple) value : new Tuple(value);

        PatternProcessor processor = new PatternProcessor(parent);
        if (processor.doRead(tuple, in, false) == ERRORED) return ERRORED;

        //Copied from declareFrom method
        for (Map.Entry<String, Obj> result : processor.results.entrySet()) {
            parent.scope.declare(result.getKey(), result.getValue());
        }

        current++;
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeWildcardPattern(OpcodeReader reader, KDataInput in) {
        position++;
        current++;
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeVariablePattern(OpcodeReader reader, KDataInput in) {
        String name = parent.stringPool.get(in.readUnsignedShort());
        Obj value;

        if (obj.hasAttr(name)) {
            value = obj.getAttr(name);
        } else if (obj.hasAttr("_" + position)) {
            value = obj.getAttr("_" + position);
        } else {
            return ERRORED;
        }

        position++;

        results.put(name, value);
        current++;
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeTuplePattern(OpcodeReader reader, KDataInput in) {
        String name = parent.stringPool.get(in.readUnsignedShort());
        Obj value;

        if (!obj.hasAttr(name)) {
            return ERRORED;
        }
        value = obj.getAttr(name);
        position++;

        Tuple tuple = new Tuple(value);

        reader.read(this, in);

        Tuple lastObj = this.obj;
        int lastSize = this.size, lastPosition = this.position, lastCurrent = this.current;
        this.obj = tuple;

        ReadResult result = READER.read(this, in);

        this.obj = lastObj;
        this.size = lastSize;
        this.position = lastPosition;
        this.current = lastCurrent;

        current++;
        return result == ENDED ? CONTINUE : result;
    }

    @Override
    public ReadResult opcodeRestPattern(OpcodeReader reader, KDataInput in) {
        String name = parent.stringPool.get(in.readUnsignedShort());
        Obj value;

        if (obj.hasAttr(name)) {
            Obj val = obj.getAttr(name);
            value = val instanceof Array ? val : Array.of(val);
        } else { // empty
            int endPosition = obj.size() - (size - (current + 1));

            if (position < endPosition) {
                Array array = new Array();

                do {
                    array.add(obj.getAttr("_" + position));
                    position++;
                } while (position < endPosition);

                value = array;
            } else {
                value = new Array();
            }
        }

        results.put(name, value);

        current++;
        return CONTINUE;
    }

    @Override
    public ReadResult opcodeValuePattern(OpcodeReader reader, KDataInput in) {
        if (!obj.hasAttr("_" + position)) return ERRORED;
        Obj value = obj.getAttr("_" + position);

        KOpcodes.READER.read(parent, in);

        Obj target = parent.stack.pop();

        position++;
        return value.equals(target) ? CONTINUE : ERRORED;
    }

    @Override
    public ReadResult opcodeDefaultPattern(OpcodeReader reader, KDataInput in) {
        String name = parent.stringPool.get(in.readUnsignedShort());

        ReadResult result = reader.readNext(this, in);

        if (result == ERRORED) {
            KOpcodes.READER.read(parent, in);

            results.put(name, parent.stack.pop());
        } else {
            KOpcodes.READER.read(parent.buffer.setOut(NullOutputStream.DATA_INSTANCE), in);
        }

        return CONTINUE;
    }

    private ReadResult doRead(Tuple tuple, KDataInput input, boolean eatOpcode) {
        this.obj = tuple;
        this.results = new LinkedHashMap<>();
        this.position = 0;
        this.current = 0;

        if (eatOpcode) {
            Opcode opcode;
            if ((opcode = READER.next(input)) != PATTERN_CASE) {
                throw new InvalidBytecodeException(opcode);
            }
        }

        this.size = input.readInt();

        return READER.read(this, input);
    }

    public boolean declareFrom(Tuple tuple, KDataInput input) {
        if (doRead(tuple, input, true) == ERRORED) return false;

        for (Map.Entry<String, Obj> result : results.entrySet()) {
            parent.scope.declare(result.getKey(), result.getValue());
        }

        results = null;

        return true;
    }

    public boolean assignFrom(Tuple tuple, KDataInput input) {
        if (doRead(tuple, input, true) == ERRORED) return false;

        for (Map.Entry<String, Obj> result : results.entrySet()) {
            parent.scope.assign(result.getKey(), result.getValue());
        }

        results = null;

        return true;
    }
}
