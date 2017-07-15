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

import xyz.avarel.aje.Precedence;
import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.Single;
import xyz.avarel.aje.ast.flow.ConditionalExpr;
import xyz.avarel.aje.ast.operations.BinaryOperation;
import xyz.avarel.aje.ast.operations.BinaryOperatorType;
import xyz.avarel.aje.ast.value.UndefinedNode;
import xyz.avarel.aje.ast.variables.AssignmentExpr;
import xyz.avarel.aje.ast.variables.Identifier;
import xyz.avarel.aje.lexer.Token;
import xyz.avarel.aje.lexer.TokenType;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;

public class AttributeParser extends BinaryParser {
    public AttributeParser() {
        super(Precedence.POSTFIX);
    }

    @Override
    public Expr parse(AJEParser parser, Single left, Token token) {
        Token name = parser.eat(TokenType.IDENTIFIER);

        if (parser.match(TokenType.ASSIGN)) {
            return new AssignmentExpr(left, name.getString(), parser.parseSingle());
        } else if (parser.match(TokenType.OPTIONAL_ASSIGN)) {
            Single getOp = new Identifier(left, token.getString());

            return new ConditionalExpr(
                    new BinaryOperation(
                            getOp,
                            UndefinedNode.VALUE,
                            BinaryOperatorType.EQUALS),
                    new AssignmentExpr(left, name.getString(), parser.parseSingle()),
                    getOp);
        }

        return new Identifier(left, name.getString());
    }
}
