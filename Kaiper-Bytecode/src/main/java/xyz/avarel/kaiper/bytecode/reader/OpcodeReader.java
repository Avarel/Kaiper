package xyz.avarel.kaiper.bytecode.reader;

import xyz.avarel.kaiper.bytecode.io.KDataInput;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.opcodes.ReservedOpcode;
import xyz.avarel.kaiper.bytecode.reader.consumers.ReadResult;
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

    protected Opcode next(KDataInput in) {
        byte id = in.readByte();
        if (id < opcodeList.length) return opcodeList[id];

        ReservedOpcode opcode = new ReservedOpcode(id);
        if (foreignOpcodes) return opcode;
        throw new InvalidBytecodeException(opcode);
    }

    public ReadResult read(OpcodeProcessor consumer, KDataInput in) {
        ReadResult r;

        //noinspection StatementWithEmptyBody
        while ((r = consumer.process(this, next(in), in)) == ReadResult.CONTINUE) ;
        return r;
    }
}
