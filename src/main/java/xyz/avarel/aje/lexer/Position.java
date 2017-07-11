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

package xyz.avarel.aje.lexer;

public class Position {
    private final long line;
    private final long lineIndex;
    private final long index;

    public Position(long index, long line, long lineIndex) {
        this.index = index;
        this.line = line;
        this.lineIndex = lineIndex;
    }

    public long getIndex() {
        return index;
    }

    public long getLineNumber() {
        return line;
    }

    public long getLineIndex() {
        return lineIndex;
    }

    @Override
    public String toString() {
        return " at " + this.index + " [line " + this.line + " : char " + this.lineIndex + "]";
    }
}
