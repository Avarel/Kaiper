/*
 *  Copyright 2017 An Tran and Adrian Todt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package xyz.avarel.kaiper.parser.parslets;

import xyz.avarel.kaiper.Precedence;
import xyz.avarel.kaiper.ast.expr.Expr;
import xyz.avarel.kaiper.ast.expr.collections.GetOperation;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.BinaryParser;
import xyz.avarel.kaiper.parser.ExprParser;

//import xyz.avarel.kaiper.ast.expr.operations.SliceOperation;

public class GetSetParser extends BinaryParser {
    public GetSetParser() {
        super(Precedence.POSTFIX);
    }

    @Override
    public Expr parse(ExprParser parser, Expr left, Token token) {
//        if (parser.match(TokenType.COLON)) {
//            return parseEnd(parser, token.getPosition(), left, NullNode.VALUE);
//        }
//
        Expr key = parser.parseExpr();
//
//        if (parser.match(TokenType.COLON)) {
//            return parseEnd(parser, token.getPosition(), left, key);
//        }

        parser.eat(TokenType.RIGHT_BRACKET);

        return new GetOperation(
                token.getPosition(),
                left,
                key
        );
    }

//    private Expr parseEnd(ExprParser parser, Position position, Expr left, Expr start) {
//        if (parser.match(TokenType.COLON)) {
//            return parseStep(parser, position, left, start, NullNode.VALUE);
//        } else if (parser.match(TokenType.RIGHT_BRACKET)) {
//            return new SliceOperation(
//                    position,
//                    left,
//                    start,
//                    NullNode.VALUE,
//                    NullNode.VALUE
//            );
//        }
//
//        Expr end = parser.parseExpr();
//
//        if (parser.match(TokenType.COLON)) {
//            return parseStep(parser, position, left, start, end);
//        }
//
//        parser.eat(TokenType.RIGHT_BRACKET);
//        return new SliceOperation(
//                position,
//                left,
//                start,
//                end,
//                NullNode.VALUE
//        );
//    }
//
//    private Expr parseStep(ExprParser parser, Position position, Expr left, Expr start, Expr end) {
//        if (parser.match(TokenType.RIGHT_BRACKET)) {
//            return new SliceOperation(
//                    position,
//                    left,
//                    start,
//                    end,
//                    NullNode.VALUE
//            );
//        }
//
//        Expr step = parser.parseExpr();
//
//        parser.eat(TokenType.RIGHT_BRACKET);
//        return new SliceOperation(
//                position,
//                left,
//                start,
//                end,
//                step
//        );
//    }
}
