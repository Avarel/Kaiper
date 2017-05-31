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

package xyz.avarel.aje.ast.flow;

import xyz.avarel.aje.parser.lexer.Position;
import xyz.avarel.aje.runtime.Obj;

public class ReturnException extends RuntimeException {
    private final Position position;
    private final Obj value;

    public ReturnException(Position position, Obj value) {
        this.position = position;
        this.value = value;
    }

    public Position getPosition() {
        return position;
    }

    public Obj getValue() {
        return value;
    }
}
