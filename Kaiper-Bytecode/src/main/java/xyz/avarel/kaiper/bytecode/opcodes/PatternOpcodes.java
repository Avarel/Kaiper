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

package xyz.avarel.kaiper.bytecode.opcodes;

import xyz.avarel.kaiper.bytecode.reader.OpcodeReader;

/**
 * The Bytecode Instructions of the Kaiper Patterns.
 *
 * @author AdrianTodt
 * @version 2.0
 */
public enum PatternOpcodes implements Opcode {
    /**
     * {@code END id;}
     * <p>Special Opcode. Means the end of a bytecode block.</p>
     */
    END,

    RESERVED_001, RESERVED_002, RESERVED_003, RESERVED_004, RESERVED_005, RESERVED_006, RESERVED_007, RESERVED_008,

    /**
     * {@code BREAKPOINT;}
     * <p>Special Opcode. Freezes execution until the resumeBreakpoint() is called again.</p>
     */
    BREAKPOINT,

    PATTERN_CASE,
    WILDCARD,
    VARIABLE,
    TUPLE,
    REST,

    VALUE,
    DEFAULT;

    public static final OpcodeReader OPCODE_READER = new OpcodeReader(values());

    public int code() {
        return ordinal();
    }
}
