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

package xyz.avarel.kaiper.vm;

import xyz.avarel.kaiper.bytecode.KaiperBytecode;
import xyz.avarel.kaiper.bytecode.io.KDataInput;
import xyz.avarel.kaiper.bytecode.io.KDataInputStream;
import xyz.avarel.kaiper.bytecode.opcodes.KOpcodes;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.vm.executor.StackMachine;

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
            KDataInputStream input = new KDataInputStream(inputStream);
            versionHeaderAndCheck(input);

            StackMachine stackMachine = new StackMachine(readStringPool(input), scope);
            KOpcodes.READER.read(stackMachine, input);
            return stackMachine.stack.peek();
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    //endregion

    //region HEADERS

    private void versionHeaderAndCheck(KDataInput input) {
        KaiperBytecode.validateInit(input);
        KaiperBytecode.validateVersion(input);
    }

    private String[] readStringPool(KDataInput input) {
        int poolSize = input.readShort();
        List<String> constants = new ArrayList<>(poolSize);

        for (int i = 0; i < poolSize; i++) {
            constants.add(input.readString());
        }

        return constants.toArray(new String[0]);
    }

    //endregion
}
