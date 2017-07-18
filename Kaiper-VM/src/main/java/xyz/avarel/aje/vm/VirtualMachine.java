package xyz.avarel.aje.vm;

import xyz.avarel.aje.bytecode.AJEBytecode;
import xyz.avarel.aje.bytecode.walker.BytecodeBatchReader;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class VirtualMachine {
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
        BytecodeBatchReader reader = new BytecodeBatchReader();
        reader.walkInsts(input, walker, stringPool, 0);

        return walker.stack.peek();
    }

    //endregion

    //region HEADERS

    private void versionHeaderAndCheck(DataInput input) throws IOException {
        AJEBytecode.validateInit(input);
        AJEBytecode.validateVersion(input);
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
