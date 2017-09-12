package xyz.avarel.kaiper.bytecode.reader;

import xyz.avarel.kaiper.bytecode.io.KDataInput;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.opcodes.ReservedOpcode;
import xyz.avarel.kaiper.bytecode.reader.processors.ReadResult;
import xyz.avarel.kaiper.exceptions.InvalidBytecodeException;

public class OpcodeReader {

    protected final Opcode[] opcodeList;
    protected final boolean foreignOpcodes;

    public OpcodeReader(Opcode[] opcodeList) {
        this(opcodeList, false);
    }

    public OpcodeReader(Opcode[] opcodeList, boolean foreignOpcodes) {
        this.opcodeList = opcodeList;
        this.foreignOpcodes = foreignOpcodes;
    }

    public Opcode next(KDataInput in) {
        byte id = in.readByte();
        if (id < opcodeList.length) return opcodeList[id];

        ReservedOpcode opcode = new ReservedOpcode(id);
        if (foreignOpcodes) return opcode;
        throw new InvalidBytecodeException(opcode);
    }

    public ReadResult read(OpcodeProcessor processor, KDataInput in) {
        ReadResult r;
        //noinspection StatementWithEmptyBody
        while ((r = readNext(processor, in)) == ReadResult.CONTINUE) ;
        return r;
    }

    public ReadResult readNext(OpcodeProcessor processor, KDataInput in) {
        return processor.process(this, next(in), in);
    }
}
