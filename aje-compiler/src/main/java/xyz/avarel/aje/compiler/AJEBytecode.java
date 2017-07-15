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

package xyz.avarel.aje.compiler;

import xyz.avarel.aje.exceptions.InvalidBytecodeException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author AdrianTodt
 */
public class AJEBytecode {
    public static int BYTECODE_VERSION_MAJOR = 1, BYTECODE_VERSION_MINOR = 0;
    public static short IDENTIFIER = 0xDAB;

    public static DataOutput initialize(DataOutput output) throws IOException {
        output.writeShort(IDENTIFIER);
        output.writeInt(BYTECODE_VERSION_MAJOR);
        output.writeInt(BYTECODE_VERSION_MINOR);
        return output;
    }

    public static DataInput validateInit(DataInput input) throws IOException {
        if (input.readShort() != IDENTIFIER) {
            throw new InvalidBytecodeException("Invalid Itentifier");
        }

        int versionMajor = input.readInt(), versionMinor = input.readInt();

        if (versionMajor != BYTECODE_VERSION_MAJOR || versionMinor > BYTECODE_VERSION_MINOR) {
            throw new InvalidBytecodeException(String.format(
                    "Unsupported Bytecode Version (Library Version: DAB%d.[0-%d]; Bytecode: DAB%d.%d)",
                    BYTECODE_VERSION_MAJOR, BYTECODE_VERSION_MINOR,
                    versionMajor, versionMinor
            ));
        }

        return input;
    }

    public static String identifier(DataInput input) throws IOException {
        if (input.readShort() != IDENTIFIER) {
            throw new InvalidBytecodeException("Invalid Itentifier");
        }

        int versionMajor = input.readInt(), versionMinor = input.readInt();

        return "DAB" + versionMajor + "." + versionMinor;
    }

    public static void finalize(DataOutput output) throws IOException {
        //END -1
        output.writeByte(0);
        output.writeInt(-1);
    }
}
