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

package xyz.avarel.aje.parser;

public class ParserFlags {
    public static final int VARIABLES = 1;
    public static final int CONTROL_FLOW = 1 << 1;
    public static final int FUNCTION_CREATION = 1 << 2;
    public static final int INVOCATION = 1 << 3;
    public static final int LOOPS = 1 << 4;
    public static final int ARRAYS = 1 << 5;
    public static final int RANGES = 1 << 6;
    public static final int DICTIONARIES = 1 << 7;
    public static final int COLLECTIONS = ARRAYS | RANGES | DICTIONARIES;
    public static final int ALL_OPTS = VARIABLES | CONTROL_FLOW | FUNCTION_CREATION | INVOCATION | LOOPS | COLLECTIONS;

    public static final ParserFlags ALL_FLAGS = new ParserFlags(ALL_OPTS);

    private final int flags;

    public ParserFlags(int flags) {
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
