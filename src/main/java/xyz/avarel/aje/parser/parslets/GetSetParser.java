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
import xyz.avarel.aje.ast.collections.GetOperation;
import xyz.avarel.aje.ast.collections.SetOperation;
import xyz.avarel.aje.ast.flow.ConditionalExpr;
import xyz.avarel.aje.ast.operations.BinaryOperation;
import xyz.avarel.aje.ast.operations.SliceOperation;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;
import xyz.avarel.aje.parser.lexer.Position;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;

public class GetSetParser extends BinaryParser {
    public GetSetParser() {
        super(Precedence.ATTRIBUTE);
    }

    @Override
    public Expr parse(AJEParser parser, Expr left, Token token) {
        if (parser.match(TokenType.COLON)) {
            return parseEnd(parser, token.getPosition(), left, null);
        }

        Expr key = parser.parseExpr();

        if (parser.match(TokenType.COLON)) {
            return parseEnd(parser, token.getPosition(), left, key);
        }

        parser.eat(TokenType.RIGHT_BRACKET);

        // SET
        if (parser.match(TokenType.ASSIGN)) {
            Expr value = parser.parseExpr();
            return new SetOperation(token.getPosition(), left, key, value);
        } else if (parser.match(TokenType.OPTIONAL_ASSIGN)) {
            Expr value = parser.parseExpr();

            Expr getOp = new GetOperation(token.getPosition(), left, key);
            return new ConditionalExpr(token.getPosition(),
                    new BinaryOperation(token.getPosition(),
                            getOp,
                            new ValueAtom(token.getPosition(), Undefined.VALUE),
                            Obj::isEqualTo),
                    new SetOperation(token.getPosition(), left, key, value),
                    getOp);
        }

        return new GetOperation(token.getPosition(), left, key);
    }

    public Expr parseEnd(AJEParser parser, Position position, Expr left, Expr start) {
        if (parser.match(TokenType.COLON)) {
            return parseStep(parser, position, left, start, null);
        } else if (parser.match(TokenType.RIGHT_BRACKET)) {
            return new SliceOperation(position, left, start, null, null);
        }

        Expr end = parser.parseExpr();

        if (parser.match(TokenType.COLON)) {
            return parseStep(parser, position, left, start, end);
        }

        parser.eat(TokenType.RIGHT_BRACKET);
        return new SliceOperation(position, left, start, end, null);
    }

    public Expr parseStep(AJEParser parser, Position position, Expr left, Expr start, Expr end) {
        if (parser.match(TokenType.RIGHT_BRACKET)) {
            return new SliceOperation(position, left, start, end, null);
        }

        Expr step = parser.parseExpr();

        parser.eat(TokenType.RIGHT_BRACKET);
        return new SliceOperation(position, left, start, end, step);
    }
}
