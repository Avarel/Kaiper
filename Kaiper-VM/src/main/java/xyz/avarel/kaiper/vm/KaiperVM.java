package xyz.avarel.kaiper.vm;

import xyz.avarel.kaiper.bytecode.KaiperBytecode;
import xyz.avarel.kaiper.bytecode.io.ByteInput;
import xyz.avarel.kaiper.bytecode.io.ByteInputStream;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.Scope;
import xyz.avarel.kaiper.vm.executor.StackMachineConsumer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class KaiperVM {
    //region DELEGATES

    public Obj executeBytecode(InputStream input, Scope scope) throws IOException {
        return readAndExecute(input, scope);
    }

    public Obj executeBytecode(byte[] input, Scope scope) throws IOException {
        return readAndExecute(new ByteArrayInputStream(input), scope);
    }

    //endregion

    //region DELEGATES WITH COMPRESSION

    public Obj executeCompressedBytecode(InputStream input, Scope scope) throws IOException {
        try (GZIPInputStream gz = new GZIPInputStream(input)) {
            return readAndExecute(gz, scope);
        }
    }

    public Obj executeCompressedBytecode(byte[] input, Scope scope) throws IOException {
        try (GZIPInputStream gz = new GZIPInputStream(new ByteArrayInputStream(input))) {
            return readAndExecute(gz, scope);
        }
    }

    //endregion

    //region MAIN METHOD

    private Obj readAndExecute(InputStream inputStream, Scope scope) throws IOException {
        try {
            ByteInputStream input = new ByteInputStream(inputStream);
            versionHeaderAndCheck(input);

            StackMachineConsumer stackMachine = new StackMachineConsumer(scope, readStringPool(input));
            OpcodeReader.DEFAULT_OPCODE_READER.read(stackMachine, input);
            return stackMachine.stack.peek();
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    //endregion

    //region HEADERS

    private void versionHeaderAndCheck(ByteInput input) throws IOException {
        KaiperBytecode.validateInit(input);
        KaiperBytecode.validateVersion(input);
    }

    private List<String> readStringPool(ByteInput input) throws IOException {
        int poolSize = input.readShort();
        List<String> constants = new ArrayList<>(poolSize);

        for (int i = 0; i < poolSize; i++) {
            constants.add(input.readString());
        }

        return constants;
    }

    //endregion
}
