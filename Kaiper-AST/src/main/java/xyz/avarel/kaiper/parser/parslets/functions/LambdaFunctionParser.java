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

package xyz.avarel.kaiper.parser.parslets.functions;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.functions.FunctionNode;
import xyz.avarel.kaiper.ast.functions.ParameterData;
import xyz.avarel.kaiper.ast.value.UndefinedNode;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.KaiperParser;
import xyz.avarel.kaiper.parser.KaiperParserUtils;
import xyz.avarel.kaiper.parser.PrefixParser;

import java.util.*;

public class LambdaFunctionParser implements PrefixParser {
    @Override
    public Expr parse(KaiperParser parser, Token token) {
        if (!parser.getParserFlags().allowFunctionCreation()) {
            throw new SyntaxException("Function creation are disabled");
        }

        if (parser.match(TokenType.RIGHT_BRACE)) {
            return new FunctionNode(token.getPosition(), Collections.emptyList(), UndefinedNode.VALUE);
        }

        // Check for arrows.
        int peek = 0;
        boolean hasArrow = false;
        lookAhead:
        while (!parser.nextIs(TokenType.RIGHT_BRACE)) {
            TokenType type = parser.peek(peek).getType();
            switch (type) {
                case IDENTIFIER:
                case COLON:
                case COMMA:
                    peek++;
                    break;
                case ARROW:
                    hasArrow = true;
                default:
                    break lookAhead;
            }
        }

        List<ParameterData> parameters = Collections.emptyList();

        if (hasArrow) {
            if (!parser.match(TokenType.ARROW)) {
                parameters = new ArrayList<>();
                Set<String> paramNames = new HashSet<>();
                boolean requireDef = false;

                do {
                    boolean rest = parser.match(TokenType.REST);
                    ParameterData parameter = KaiperParserUtils.parseParameter(parser, requireDef);

                    if (parameter.getDefault() != null) {
                        requireDef = true;
                    }

                    if (paramNames.contains(parameter.getName())) {
                        throw new SyntaxException("Duplicate parameter name", parser.getLast().getPosition());
                    } else {
                        paramNames.add(parameter.getName());
                    }

                    if (rest && parser.match(TokenType.COMMA)) {
                        throw new SyntaxException("Rest parameters must be the last parameter",
                                parser.peek(0).getPosition());
                    }

                    parameters.add(parameter);
                } while (parser.match(TokenType.COMMA));

                parser.eat(TokenType.ARROW);
            }
        } else {
            parameters = Collections.singletonList(new ParameterData("it"));
        }

        Expr expr = parser.parseStatements();

        parser.eat(TokenType.RIGHT_BRACE);

        return new FunctionNode(token.getPosition(), parameters, expr);
    }
}
