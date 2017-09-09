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

/**
 * The Bytecode Instructions of the Kaiper Bytecode.
 *
 * @author AdrianTodt
 * @version 2.0
 */
public enum Opcodes implements DataOutputConsumer, Opcode {
    /**
     * {@code END id;}
     * <p>Special Opcode. Means the end of a bytecode block.</p>
     */
    END,
    /**
     * {@code LINE_NUMBER number;}
     * <p>Special Opcode. Sets the line number to the equivalent line on the source code.</p>
     */
    LINE_NUMBER,

    RESERVED_002, RESERVED_003, RESERVED_004, RESERVED_005, RESERVED_006, RESERVED_007, RESERVED_008, RESERVED_009,

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

    RESERVED_012, RESERVED_013, RESERVED_014, RESERVED_015, RESERVED_016, RESERVED_017,

    DUP,
    POP,

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
    /**
     * {@code I_CONST value;}
     * <p>Summons a Int Constant.</p>
     * <b>Parameters:</b>
     * <ul>
     * <li>{@code  value} - The value.</li>
     * </ul>
     * <b>Action:</b>
     * <ul>
     * <li>Pushes the {@code int} object into the stack.</li>
     * </ul>
     */
    I_CONST,
    /**
     * {@code D_CONST value;}
     * <p>Summons a Decimal Constant.</p>
     * <b>Parameters:</b>
     * <ul>
     * <li>{@code  value} - The value.</li>
     * </ul>
     * <b>Action:</b>
     * <ul>
     * <li>Pushes the {@code decimal} object into the stack.</li>
     * </ul>
     */
    D_CONST,
    /**
     * {@code S_CONST index;}
     * <p>Summons a String constant.</p>
     * <b>Parameters:</b>
     * <ul>
     * <li>{@code  index} - The index of the String in the String Pool.</li>
     * </ul>
     * <b>Action:</b>
     * <ul>
     * <li>Pushes the {@code string} object into the stack.</li>
     * </ul>
     */
    S_CONST,

    RESERVED_025, RESERVED_026, RESERVED_027, RESERVED_028, RESERVED_029,

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
     * <li>Pushes 2 Objects from the stack (right, left);</li>
     * <li>Pushes the new {@code Range} into the stack.</li>
     * </ul>
     */
    NEW_RANGE,
    NEW_FUNCTION,
    NEW_MODULE,
    NEW_TYPE,
    NEW_TUPLE,

    RESERVED_037, RESERVED_038, RESERVED_039,

    DECLARE, // todo(Adrian) DOC
    ASSIGN,
    IDENTIFIER,
    /**
     * {@code ARRAY_GET;}
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
    ARRAY_GET,
    /**
     * {@code ARRAY_SET;}
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
    ARRAY_SET,

    RESERVED_045, RESERVED_046, RESERVED_047, RESERVED_048, RESERVED_049,

    /**
     * FIXME: 04/09/2017 Documentation misleading
     * {@code INVOKE;}
     * <p>Creates a Range.</p>
     * <b>Parameters:</b>
     * <ul>
     * <li>{@code pCount} - The Parameter count.</li>
     * </ul>
     * <b>Action:</b>
     * <ul>
     * <li>Pushes {@code pCount + 1} Objects from the stack (left, args);</li>
     * <li>Execute the Invocation on it;</li>
     * <li>Pushes the new Result into the stack.</li>
     * </ul>
     */
    INVOKE,
    /**
     * {@code UNARY_OPERATION type;}
     * <p>Executes a Unary Operation.</p>
     * <b>Parameters:</b>
     * <ul>
     * <li>{@code type} - The UnaryOperatorType index.</li>
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
     * <li>{@code type} - The BinaryOperatorType index.</li>
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

    RESERVED_054, RESERVED_055, RESERVED_056, RESERVED_057, RESERVED_058, RESERVED_059,

    CONDITIONAL,
    FOR_EACH,
    WHILE,

    RESERVED_063, RESERVED_064, RESERVED_065, RESERVED_066, RESERVED_067, RESERVED_068, RESERVED_069;

    public static Opcode byId(int id) {
        Opcodes[] values = values();
        if (id < values.length) return values[id];
        return new ReservedOpcode(id);
    }

    public int code() {
        return ordinal();
    }
}
