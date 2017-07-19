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

package xyz.avarel.kaiper.parser.parslets;

import xyz.avarel.kaiper.Precedence;
import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.ast.collections.GetOperation;
import xyz.avarel.kaiper.ast.collections.SetOperation;
import xyz.avarel.kaiper.ast.flow.ConditionalExpr;
import xyz.avarel.kaiper.ast.operations.BinaryOperation;
import xyz.avarel.kaiper.operations.BinaryOperatorType;
import xyz.avarel.kaiper.ast.operations.SliceOperation;
import xyz.avarel.kaiper.ast.value.UndefinedNode;
import xyz.avarel.kaiper.lexer.Position;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.AJEParser;
import xyz.avarel.kaiper.parser.BinaryParser;

public class GetSetParser extends BinaryParser {
    public GetSetParser() {
        super(Precedence.POSTFIX);
    }

    @Override
    public Expr parse(AJEParser parser, Single left, Token token) {
        if (parser.match(TokenType.COLON)) {
            return parseEnd(parser, token.getPosition(), left, UndefinedNode.VALUE);
        }

        Single key = parser.parseSingle();

        if (parser.match(TokenType.COLON)) {
            return parseEnd(parser, token.getPosition(), left, key);
        }

        parser.eat(TokenType.RIGHT_BRACKET);

        // SET
        if (parser.match(TokenType.ASSIGN)) {
            Single value = parser.parseSingle();
            return new SetOperation(left, key, value);
        } else if (parser.match(TokenType.OPTIONAL_ASSIGN)) {
            Single value = parser.parseSingle();

            Single getOp = new GetOperation(left, key);
            return new ConditionalExpr(
                    new BinaryOperation(
                            getOp,
                            UndefinedNode.VALUE,
                            BinaryOperatorType.EQUALS),
                    new SetOperation(left, key, value),
                    getOp);
        }

        return new GetOperation(left, key);
    }

    public Single parseEnd(AJEParser parser, Position position, Single left, Single start) {
        if (parser.match(TokenType.COLON)) {
            return parseStep(parser, position, left, start, UndefinedNode.VALUE);
        } else if (parser.match(TokenType.RIGHT_BRACKET)) {
            return new SliceOperation(left, start, UndefinedNode.VALUE, UndefinedNode.VALUE);
        }

        Single end = parser.parseSingle();

        if (parser.match(TokenType.COLON)) {
            return parseStep(parser, position, left, start, end);
        }

        parser.eat(TokenType.RIGHT_BRACKET);
        return new SliceOperation(left, start, end, UndefinedNode.VALUE);
    }

    public Single parseStep(AJEParser parser, Position position, Single left, Single start, Single end) {
        if (parser.match(TokenType.RIGHT_BRACKET)) {
            return new SliceOperation(left, start, end, UndefinedNode.VALUE);
        }

        Single step = parser.parseSingle();

        parser.eat(TokenType.RIGHT_BRACKET);
        return new SliceOperation(left, start, end, step);
    }
}
