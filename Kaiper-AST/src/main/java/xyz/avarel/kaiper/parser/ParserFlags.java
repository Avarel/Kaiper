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

package xyz.avarel.kaiper.parser;

public class ParserFlags {
    public static final short VARIABLES = 1;
    public static final short CONTROL_FLOW = 1 << 1;
    public static final short FUNCTION_CREATION = 1 << 2;
    public static final short INVOCATION = 1 << 3;
    public static final short LOOPS = 1 << 4;
    public static final short ARRAYS = 1 << 5;
    public static final short RANGES = 1 << 6;
    public static final short DICTIONARIES = 1 << 7;
    public static final short COLLECTIONS = ARRAYS | RANGES | DICTIONARIES;
    public static final short ALL_OPTS = VARIABLES | CONTROL_FLOW | FUNCTION_CREATION | INVOCATION | LOOPS | COLLECTIONS;

    public static final ParserFlags ALL_FLAGS = new ParserFlags(ALL_OPTS);

    private final short flags;

    public ParserFlags(short flags) {
        this.flags = flags;
    }

    public boolean allowArray() {
        return (flags & ARRAYS) == ARRAYS;
    }

    public boolean allowRanges() {
        return (flags & RANGES) == RANGES;
    }

    public boolean allowDictionary() {
        return (flags & DICTIONARIES) == DICTIONARIES;
    }

    public boolean allowVariables() {
        return (flags & VARIABLES) == VARIABLES;
    }

    public boolean allowFunctionCreation() {
        return (flags & FUNCTION_CREATION) == FUNCTION_CREATION;
    }

    public boolean allowInvocation() {
        return (flags & INVOCATION) == INVOCATION;
    }

    public boolean allowControlFlows() {
        return (flags & CONTROL_FLOW) == CONTROL_FLOW;
    }

    public boolean allowLoops() {
        return (flags & LOOPS) == LOOPS;
    }
}
