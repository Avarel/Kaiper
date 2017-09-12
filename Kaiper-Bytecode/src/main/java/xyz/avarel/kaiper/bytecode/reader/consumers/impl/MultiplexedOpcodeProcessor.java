package xyz.avarel.kaiper.bytecode.reader.consumers.impl;

import xyz.avarel.kaiper.bytecode.io.KDataInput;
import xyz.avarel.kaiper.bytecode.opcodes.Opcode;
import xyz.avarel.kaiper.bytecode.reader.OpcodeProcessor;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.bytecode.reader.consumers.ReadResult;
import xyz.avarel.kaiper.utils.ArrayIterator;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Iterator;

public class MultiplexedOpcodeProcessor implements OpcodeProcessor {
    private final OpcodeProcessor[] consumers;

    public MultiplexedOpcodeProcessor(OpcodeProcessor[] consumers) {
        this.consumers = consumers;
    }

    @Override
    public ReadResult process(OpcodeReader reader, Opcode opcode, KDataInput in) {
        InputStream stream = in.getInputStream();
        if (!stream.markSupported()) {
            throw new IllegalArgumentException("Mark not supported; Required for Multiplexing");
        }

        Iterator<OpcodeProcessor> iterator = new ArrayIterator<>(consumers);

        ReadResult result = null;

        while (iterator.hasNext()) {
            OpcodeProcessor next = iterator.next();

            if (iterator.hasNext()) {
                stream.mark(0);
            }

            result = next.process(reader, opcode, in);

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
