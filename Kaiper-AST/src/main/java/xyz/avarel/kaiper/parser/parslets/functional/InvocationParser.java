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
import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.ast.invocation.Invocation;
import xyz.avarel.kaiper.ast.tuples.TupleExpr;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.BinaryParser;
import xyz.avarel.kaiper.parser.KaiperParser;

import java.util.Collections;

public class InvocationParser extends BinaryParser {
    public InvocationParser() {
        super(Precedence.POSTFIX);
    }

    @Override
    public Expr parse(KaiperParser parser, Single left, Token token) {
        if (!parser.getParserFlags().allowInvocation()) {
            throw new SyntaxException("Function creation are disabled");
        }

        Single argument;

        if (parser.match(TokenType.RIGHT_PAREN)) {
            argument = new TupleExpr(left.getPosition(), Collections.emptyMap());
        } else {
            argument = parser.parseSingle();
            parser.eat(TokenType.RIGHT_PAREN);
        }

        return InvocationParser.tupleInvocationCheck(token, left, argument);
    }

    public static Invocation tupleInvocationCheck(Token token, Single left, Single argument) {
        if (argument instanceof TupleExpr) {
            return new Invocation(token.getPosition(), left, (TupleExpr) argument);
        } else {
            return new Invocation(token.getPosition(), left,
                    new TupleExpr(argument.getPosition(), Collections.singletonMap("value", argument))
            );
        }
    }
}
