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

package xyz.avarel.kaiper.parser.parslets.variables;

import xyz.avarel.kaiper.Precedence;
import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.ast.invocation.Invocation;
import xyz.avarel.kaiper.ast.tuples.TupleExpr;
import xyz.avarel.kaiper.ast.variables.Identifier;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.KaiperParser;
import xyz.avarel.kaiper.parser.PrefixParser;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class NameParser implements PrefixParser {
    @Override
    public Expr parse(KaiperParser parser, Token token) {
        Identifier id = new Identifier(token.getPosition(), token.getString());

        if (parser.match(TokenType.COLON)) {
            Map<String, Single> map = new LinkedHashMap<>();

            Single value = parser.parseSingle(Precedence.TUPLE_PAIR);
            map.put(id.getName(), value);

            while (parser.match(TokenType.COMMA)) {
                Token name = parser.eat(TokenType.IDENTIFIER);
                parser.eat(TokenType.COLON);
                value = parser.parseSingle(Precedence.TUPLE_PAIR);
                if (map.put(name.getString(), value) != null) {
                    throw new SyntaxException("Duplicate tuple field name", name.getPosition());
                }
            }

            return new TupleExpr(token.getPosition(), map);
        }

        if (parser.nextIs(TokenType.IDENTIFIER)) {
            Single argument = parser.parseSingle();
            return new Invocation(token.getPosition(), id, argument);
        } else if (parser.nextIsAny(TokenType.STRING, TokenType.INT,
                TokenType.NUMBER, TokenType.LEFT_BRACE, TokenType.FUNCTION)) {
            Single argument = parser.parseSingle();
            return new Invocation(
                    token.getPosition(),
                    id,
                    new TupleExpr(
                            argument.getPosition(),
                            Collections.singletonMap(
                                    "value",
                                    argument
                            )
                    )
            );
        }

        return id;
    }
}
