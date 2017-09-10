/*
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package xyz.avarel.kaiper.bytecode;

import xyz.avarel.kaiper.bytecode.io.ByteInput;
import xyz.avarel.kaiper.bytecode.io.ByteOutput;
import xyz.avarel.kaiper.bytecode.opcodes.Opcodes;
import xyz.avarel.kaiper.exceptions.InvalidBytecodeException;

import java.nio.charset.StandardCharsets;

import static xyz.avarel.kaiper.bytecode.BytecodeUtils.toHex;

/**
 * @author AdrianTodt
 */
public class KaiperBytecode {
    public static String IDENTIFIER = "Kai";
    public static byte[] IDENTIFIER_BYTES = IDENTIFIER.getBytes(StandardCharsets.UTF_8);
    public static byte BYTECODE_VERSION_MAJOR = 2, BYTECODE_VERSION_MINOR = 0;
    public static String IDENTIFIER_HEX = "0x" + toHex(IDENTIFIER_BYTES);

    public static void initialize(ByteOutput output) {
        output.writeBytes(IDENTIFIER_BYTES).writeByte(BYTECODE_VERSION_MAJOR).writeByte(BYTECODE_VERSION_MINOR);
    }

    public static void validateInit(ByteInput input) {
        byte k = input.readByte(), a = input.readByte(), i = input.readByte();

        if (k != 'K' || a != 'a' || i != 'i') {
            String headerHex = "0x" + toHex(k, a, i);

            throw new InvalidBytecodeException("Invalid Header " + headerHex + ", was expecting " + IDENTIFIER_HEX + " (" + IDENTIFIER + ")");
        }
    }

    public static void validateVersion(ByteInput input) {
        int versionMajor = input.readByte(), versionMinor = input.readByte();

        if (versionMajor != BYTECODE_VERSION_MAJOR || versionMinor > BYTECODE_VERSION_MINOR) {
            throw new InvalidBytecodeException(String.format(
                    "Unsupported Bytecode Version (Library Version: DAB%d.[0-%d]; Bytecode: DAB%d.%d)",
                    BYTECODE_VERSION_MAJOR, BYTECODE_VERSION_MINOR,
                    versionMajor, versionMinor
            ));
        }
    }

    public static String identifier(ByteInput input) {
        validateInit(input);

        int versionMajor = input.readByte(), versionMinor = input.readByte();

        return IDENTIFIER + versionMajor + "." + versionMinor;
    }

    public static void finalize(ByteOutput output) {
        output.writeOpcode(Opcodes.END).writeShort(-1);
    }
}
