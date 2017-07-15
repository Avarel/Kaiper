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

import xyz.avarel.aje.ast.collections.RangeNode;
import xyz.avarel.aje.ast.operations.BinaryOperatorType;
import xyz.avarel.aje.ast.operations.UnaryOperatorType;
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
     * {@code RETURN;}
     * <p>Stops the action, returning an object.</p>
     * <b>Parameters:</b>
     * <ul>
     * <li>none</li>
     * </ul>
     * <b>Action:</b>
     * <ul>
     * <li>Retrieves the Top-most Object on the stack;</li>
     * <li>Returns it, stopping the execution of the Code.</li>
     * </ul>
     */
    RETURN,
    /**
     * {@code U_CONST;}
     * <p>Summons the <b>Undefined</b> constant.</p>
     * <b>Parameters:</b>
     * <ul>
     * <li>none</li>
     * </ul>
     * <b>Action:</b>
     * <ul>
     * <li>Pushes the {@code undefined} constant into the stack.</li>
     * </ul>
     */
    U_CONST,
    /**
     * {@code B_CONST_TRUE;}
     * <p>Summons the <b>True</b> constant.</p>
     * <b>Parameters:</b>
     * <ul>
     * <li>none</li>
     * </ul>
     * <b>Action:</b>
     * <ul>
     * <li>Pushes the {@code true} constant into the stack.</li>
     * </ul>
     */
    B_CONST_TRUE,
    /**
     * {@code B_CONST_FALSE;}
     * <p>Summons the <b>False</b> constant.</p>
     * <b>Parameters:</b>
     * <ul>
     * <li>none</li>
     * </ul>
     * <b>Action:</b>
     * <ul>
     * <li>Pushes the {@code false} constant into the stack.</li>
     * </ul>
     */
    B_CONST_FALSE,
    I_CONST, //TODO DOC
    D_CONST, //TODO DOC
    S_CONST, //TODO DOC
    /**
     * {@code NEW_ARRAY;}
     * <p>Creates an Array.</p>
     * <b>Parameters:</b>
     * <ul>
     * <li>none</li>
     * </ul>
     * <b>Action:</b>
     * <ul>
     * <li>Pushes the new {@code Array} into the stack.</li>
     * </ul>
     */
    NEW_ARRAY,
    /**
     * {@code NEW_DICTIONARY;}
     * <p>Creates a Dictionary.</p>
     * <b>Parameters:</b>
     * <ul>
     * <li>none</li>
     * </ul>
     * <b>Action:</b>
     * <ul>
     * <li>Pushes the new {@code Dictionary} into the stack.</li>
     * </ul>
     */
    NEW_DICTIONARY,
    /**
     * {@code NEW_RANGE;}
     * <p>Creates a Range.</p>
     * <b>Parameters:</b>
     * <ul>
     * <li>none</li>
     * </ul>
     * <b>Action:</b>
     * <ul>
     * <li>Retrieves the Top-most Object on the stack ({@link RangeNode#getRight()});</li>
     * <li>Retrieves the Top-most Object on the stack ({@link RangeNode#getLeft()});</li>
     * <li>Pushes the new {@code Range} into the stack.</li>
     * </ul>
     */
    NEW_RANGE,
    INVOKE,
    DECLARE,
    /**
     * {@code UNARY_OPERATION type;}
     * <p>Executes a Unary Operation.</p>
     * <b>Parameters:</b>
     * <ul>
     * <li>{@code type} - The {@link UnaryOperatorType} index.</li>
     * </ul>
     * <b>Action:</b>
     * <ul>
     * <li>Pushes an Object from the stack;</li>
     * <li>Execute the Unary Operation on it;</li>
     * <li>Pushes the new Object into the stack.</li>
     * </ul>
     */
    UNARY_OPERATION,
    /**
     * {@code BINARY_OPERATION type;}
     * <p>Executes a Binary Operation.</p>
     * <b>Parameters:</b>
     * <ul>
     * <li>{@code type} - The {@link BinaryOperatorType} index.</li>
     * </ul>
     * <b>Action:</b>
     * <ul>
     * <li>Pushes 2 Objects from the stack (right, left);</li>
     * <li>Execute the Binary Operation on them;</li>
     * <li>Pushes the new Object into the stack.</li>
     * </ul>
     */
    BINARY_OPERATION,
    /**
     * {@code SLICE_OPERATION;}
     * <p>Executes a Slice Operation.</p>
     * <b>Parameters:</b>
     * <ul>
     * <li>none</li>
     * </ul>
     * <b>Action:</b>
     * <ul>
     * <li>Pushes 4 Objects from the stack ({@code step}, {@code end}, {@code start}, {@code left});</li>
     * <li>Execute the Slice Operation on the {@code left};</li>
     * <li>Pushes the new Object into the stack.</li>
     * </ul>
     */
    SLICE_OPERATION,
    /**
     * {@code GET;}
     * <p>Executes a Get Operation.</p>
     * <b>Parameters:</b>
     * <ul>
     * <li>none</li>
     * </ul>
     * <b>Action:</b>
     * <ul>
     * <li>Pushes 2 Objects from the stack (key, left);</li>
     * <li>Execute the Get Operation on them;</li>
     * <li>Pushes the new Object into the stack.</li>
     * </ul>
     */
    GET,
    /**
     * {@code SET;}
     * <p>Executes a Get Operation.</p>
     * <b>Parameters:</b>
     * <ul>
     * <li>none</li>
     * </ul>
     * <b>Action:</b>
     * <ul>
     * <li>Pushes 2 Objects from the stack (key, left);</li>
     * <li>Execute the Get Operation on them;</li>
     * <li>Pushes the new Object into the stack.</li>
     * </ul>
     */
    SET,

    DUP, SWAP, POP;

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
