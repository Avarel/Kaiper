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
import xyz.avarel.kaiper.bytecode.io.NullOutputStream;
import xyz.avarel.kaiper.bytecode.opcodes.KOpcodes;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.bytecode.reader.processors.PatternOpcodeProcessorAdapter;
import xyz.avarel.kaiper.bytecode.reader.processors.ReadResult;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Tuple;
import xyz.avarel.kaiper.runtime.collections.Array;

import java.util.LinkedHashMap;
import java.util.Map;

import static xyz.avarel.kaiper.bytecode.opcodes.PatternOpcodes.READER;
import static xyz.avarel.kaiper.bytecode.reader.processors.ReadResult.*;
import static xyz.avarel.kaiper.vm.executor.StackMachine.assign;
import static xyz.avarel.kaiper.vm.executor.StackMachine.declare;

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
    public ReadResult opcodeVariablePattern(OpcodeReader reader, KDataInput in) {
        String name = parent.stringPool[in.readUnsignedShort()];
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
        String name = parent.stringPool[in.readUnsignedShort()];
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
        String name = parent.stringPool[in.readUnsignedShort()];
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
        String name = parent.stringPool[in.readUnsignedShort()];

        ReadResult result = reader.readNext(this, in);

        if (result == ERRORED) {
            KOpcodes.READER.read(parent, in);

            results.put(name, parent.stack.pop());
        } else {
            KOpcodes.READER.read(parent.buffer.setOut(NullOutputStream.DATA_INSTANCE), in);
        }

        return CONTINUE;
    }

    private ReadResult doRead(Tuple tuple, int size, KDataInput input) {
        this.obj = tuple;
        this.results = new LinkedHashMap<>();
        this.position = 0;
        this.current = 0;

        return READER.read(this, input);
    }

    public boolean declareFrom(Tuple tuple, int size, KDataInput input) {
        if (doRead(tuple, size, input) == ERRORED) return false;

        for (Map.Entry<String, Obj> result : results.entrySet()) {
            declare(parent.scope, result.getKey(), result.getValue());
        }

        results = null;

        return true;
    }

    public boolean assignFrom(Tuple tuple, int size, KDataInput input) {
        if (doRead(tuple, size, input) == ERRORED) return false;

        for (Map.Entry<String, Obj> result : results.entrySet()) {
            assign(parent.scope, result.getKey(), result.getValue());
        }

        results = null;

        return true;
    }
}
