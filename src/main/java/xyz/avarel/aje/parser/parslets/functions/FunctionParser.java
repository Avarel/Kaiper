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

package xyz.avarel.aje.parser.parslets.functions;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ValueAtom;
import xyz.avarel.aje.ast.functions.FunctionAtom;
import xyz.avarel.aje.ast.functions.ParameterData;
import xyz.avarel.aje.ast.variables.Identifier;
import xyz.avarel.aje.exceptions.SyntaxException;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.Obj;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FunctionParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        List<ParameterData> parameters = new ArrayList<>();

        String name = null;
        if (parser.match(TokenType.IDENTIFIER)) {
            name = parser.getLast().getString();
        }

        parser.eat(TokenType.LEFT_PAREN);
        if (!parser.match(TokenType.RIGHT_PAREN)) {
            Set<String> paramNames = new HashSet<>();
            boolean requireDef = false;

            do {
                String parameterName = parser.eat(TokenType.IDENTIFIER).getString();

                if (paramNames.contains(parameterName)) {
                    throw new SyntaxException("Duplicate parameter name", parser.getLast().getPosition());
                } else {
                    paramNames.add(parameterName);
                }

                Expr parameterType = new ValueAtom(parser.peek(0).getPosition(), Obj.TYPE);
                Expr parameterDefault = null;

                if (parser.match(TokenType.COLON)) {
                    Token typeToken = parser.eat(TokenType.IDENTIFIER);
                    parameterType = new Identifier(typeToken.getPosition(), typeToken.getString());
                    while (parser.match(TokenType.DOT)) {
                        parameterType = new Identifier(typeToken.getPosition(), parameterType, parser.eat(TokenType.IDENTIFIER).getString());
                    }
                }
                if (parser.match(TokenType.ASSIGN)) {
                    parameterDefault = parser.parseExpr();
                    requireDef = true;
                } else if (requireDef) {
                    throw new SyntaxException("All parameters after the first default requires a default", parser.peek(0).getPosition());
                }

                ParameterData parameter = new ParameterData(parameterName, parameterType, parameterDefault);
                parameters.add(parameter);
            } while (parser.match(TokenType.COMMA));
            parser.match(TokenType.RIGHT_PAREN);
        }

        Expr expr;

        if (parser.match(TokenType.ASSIGN)) {
            if (parser.match(TokenType.LEFT_BRACE)) {
                expr = parser.parseStatements();
                parser.eat(TokenType.RIGHT_BRACE);
            } else {
                expr = parser.parseExpr();
            }
        } else if (parser.match(TokenType.LEFT_BRACE)) {
            expr = parser.parseStatements();
            parser.eat(TokenType.RIGHT_BRACE);
        } else {
            expr = parser.parseStatements();
        }

        return new FunctionAtom(token.getPosition(), name, parameters, expr);
    }
}
