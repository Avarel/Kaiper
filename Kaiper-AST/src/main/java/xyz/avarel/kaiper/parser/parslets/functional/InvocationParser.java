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

package xyz.avarel.kaiper.parser.parslets.functional;

import xyz.avarel.kaiper.Precedence;
import xyz.avarel.kaiper.ast.expr.Expr;
import xyz.avarel.kaiper.ast.expr.invocation.Invocation;
import xyz.avarel.kaiper.ast.expr.tuples.TupleExpr;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.BinaryParser;
import xyz.avarel.kaiper.parser.ExprParser;

import java.util.Collections;

public class InvocationParser extends BinaryParser {
    public static final TokenType[] argumentTokens = {
            TokenType.IDENTIFIER, TokenType.STRING, TokenType.INT,
            TokenType.NUMBER, TokenType.FUNCTION, TokenType.NULL
    };

    public InvocationParser() {
        super(Precedence.POSTFIX);
    }

    @Override
    public Expr parse(ExprParser parser, Expr left, Token token) {
        Expr argument;

        if (parser.match(TokenType.RIGHT_PAREN)) {
            argument = new TupleExpr(left.getPosition(), Collections.emptyList());
        } else {
            argument = parser.parseExpr();
            parser.eat(TokenType.RIGHT_PAREN);
        }

        return InvocationParser.tupleInvocationCheck(left, argument);
    }

    public static Invocation tupleInvocationCheck(Expr left, Expr argument) {
        if (argument instanceof TupleExpr) {
            return new Invocation(argument.getPosition(), left, (TupleExpr) argument);
        } else {
            return new Invocation(argument.getPosition(), left,
                    new TupleExpr(argument.getPosition(), Collections.singletonList(argument))
            );
        }
    }
}
