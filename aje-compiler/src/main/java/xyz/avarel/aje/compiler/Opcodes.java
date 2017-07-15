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

import java.io.DataOutput;
import java.io.IOException;

/**
 * Custom bytecode instructions.
 *
 * @author AdrianTodt
 */
public enum Opcodes implements DataOutputConsumer {
    END,
    /**
     * <p>Stops the action, returning an object.</p>
     * <b>Parameters:</b>
     * <ul>
     *     <li>none</li>
     * </ul>
     * <b>Action:</b>
     * <ul>
     *     <li>Retrieves the Top-most Object on the stack;</li>
     *     <li>Returns it, stopping the execution of the Code.</li>
     * </ul>
     */
    RETURN,
    /**
     * <p>Summons the <b>Undefined</b> constant.</p>
     * <b>Parameters:</b>
     * <ul>
     *     <li>none</li>
     * </ul>
     * <b>Action:</b>
     * <ul>
     *     <li>Pushes the {@code undefined} constant into the stack.</li>
     * </ul>
     */
    U_CONST,
    /**
     * <p>Summons the <b>True</b> constant.</p>
     * <b>Parameters:</b>
     * <ul>
     *     <li>none</li>
     * </ul>
     * <b>Action:</b>
     * <ul>
     *     <li>Pushes the {@code true} constant into the stack.</li>
     * </ul>
     */
    B_CONST_TRUE,
    /**
     * <p>Summons the <b>False</b> constant.</p>
     * <b>Parameters:</b>
     * <ul>
     *     <li>none</li>
     * </ul>
     * <b>Action:</b>
     * <ul>
     *     <li>Pushes the {@code false} constant into the stack.</li>
     * </ul>
     */
    B_CONST_FALSE,
    I_CONST,
    D_CONST,
    S_CONST,
    NEW_ARRAY,
    NEW_DICTIONARY,
    NEW_RANGE,
    INVOKE;

    public static Opcodes byID(byte id) {
        Opcodes[] values = values();
        if (id < values.length) return values[id];
        throw new InvalidBytecodeException("Invalid Instruction");
    }

    @Override
    public void writeInto(DataOutput out) throws IOException {
        out.writeByte(ordinal());
    }
}
