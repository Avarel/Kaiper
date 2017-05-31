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

package xyz.avarel.aje.parser.parslets.variables;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.ValueAtom;
import xyz.avarel.aje.ast.flow.ConditionalExpr;
import xyz.avarel.aje.ast.operations.BinaryOperation;
import xyz.avarel.aje.ast.variables.AssignmentExpr;
import xyz.avarel.aje.ast.variables.Identifier;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;

public class NameParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        if (parser.match(TokenType.ASSIGN)) {
            return new AssignmentExpr(token.getPosition(), null, token.getString(), parser.parseExpr(), false);
        } else if (parser.match(TokenType.OPTIONAL_ASSIGN)) {

            Expr getOp = new Identifier(token.getPosition(), token.getString());

            return new ConditionalExpr(token.getPosition(),
                    new BinaryOperation(token.getPosition(),
                            getOp,
                            new ValueAtom(token.getPosition(), Undefined.VALUE),
                            Obj::isEqualTo),
                    new AssignmentExpr(token.getPosition(), null, token.getString(), parser.parseExpr(), false),
                    getOp);
        }

        return new Identifier(token.getPosition(), token.getString());
    }
}
