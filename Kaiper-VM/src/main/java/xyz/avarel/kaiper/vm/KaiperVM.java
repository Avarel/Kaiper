package xyz.avarel.kaiper.vm;

import xyz.avarel.kaiper.bytecode.KaiperBytecode;
import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.Scope;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class KaiperVM {
    //region DELEGATES

    public Obj executeBytecode(DataInput input, Scope scope) throws IOException {
        return readAndExecute(input, scope);
    }

    public Obj executeBytecode(InputStream input, Scope scope) throws IOException {
        return readAndExecute(new DataInputStream(input), scope);
    }

    public Obj executeBytecode(byte[] input, Scope scope) throws IOException {
        return readAndExecute(new DataInputStream(new ByteArrayInputStream(input)), scope);
    }

    //endregion

    //region DELEGATES WITH COMPRESSION

    public Obj executeCompressedBytecode(InputStream input, Scope scope) throws IOException {
        try (GZIPInputStream gz = new GZIPInputStream(input)) {
            return readAndExecute(new DataInputStream(gz), scope);
        }
    }

    public Obj executeCompressedBytecode(byte[] input, Scope scope) throws IOException {
        try (GZIPInputStream gz = new GZIPInputStream(new ByteArrayInputStream(input))) {
            return readAndExecute(new DataInputStream(gz), scope);
        }
    }

    //endregion

    //region MAIN METHOD

    private Obj readAndExecute(DataInput input, Scope scope) throws IOException {
        StackMachineWalker walker = new StackMachineWalker(scope);

        versionHeaderAndCheck(input);
        List<String> stringPool = stringPool(input);
        OpcodeReader reader = new OpcodeReader(opcodes, foreignOpcodes);
        reader.read(input, walker, stringPool, 0);

        return walker.stack.peek();
    }

    //endregion

    //region HEADERS

    private void versionHeaderAndCheck(DataInput input) throws IOException {
        KaiperBytecode.validateInit(input);
        KaiperBytecode.validateVersion(input);
    }

    private List<String> stringPool(DataInput input) throws IOException {
        int poolSize = input.readShort();
        List<String> constants = new ArrayList<>(poolSize);

        for (int i = 0; i < poolSize; i++) {
            constants.add(input.readUTF());
        }

        return constants;
    }

    //endregion
}
