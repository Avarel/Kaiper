package xyz.avarel.kaiper.bytecode.reader.consumers.impl;

import xyz.avarel.kaiper.bytecode.io.ByteInput;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.reader.BaseOpcodeConsumer;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.bytecode.reader.consumers.ReadResult;
import xyz.avarel.kaiper.utils.ArrayIterator;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Iterator;

public class MultiplexedOpcodeConsumer implements BaseOpcodeConsumer {
    private final BaseOpcodeConsumer[] consumers;

    public MultiplexedOpcodeConsumer(BaseOpcodeConsumer[] consumers) {
        this.consumers = consumers;
    }

    @Override
    public ReadResult accept(OpcodeReader reader, Opcode opcode, ByteInput in) {
        InputStream stream = in.getInputStream();
        if (!stream.markSupported()) {
            throw new IllegalArgumentException("Mark not supported; Required for Multiplexing");
        }

        Iterator<BaseOpcodeConsumer> iterator = new ArrayIterator<>(consumers);

        ReadResult result = null;

        while (iterator.hasNext()) {
            BaseOpcodeConsumer next = iterator.next();

            if (iterator.hasNext()) {
                stream.mark(0);
            }

            result = next.accept(reader, opcode, in);

            if (iterator.hasNext()) {
                try {
                    stream.reset();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }

        return result;
    }
}
