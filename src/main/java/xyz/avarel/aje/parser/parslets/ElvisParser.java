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

package xyz.avarel.aje.parser.parslets;

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ValueAtom;
import xyz.avarel.aje.ast.flow.ConditionalExpr;
import xyz.avarel.aje.ast.operations.BinaryOperation;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;

public class ElvisParser extends BinaryParser {
    public ElvisParser() {
        super(Precedence.INFIX, true);
    }

    @Override
    public Expr parse(AJEParser parser, Expr left, Token token) {
        return new ConditionalExpr(token.getPosition(),
                new BinaryOperation(token.getPosition(),
                    left,
                    new ValueAtom(token.getPosition(), Undefined.VALUE),
                    Obj::isEqualTo),
                parser.parseExpr(),
                left);
    }
}
