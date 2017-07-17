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
import xyz.avarel.aje.ast.functions.FunctionNode;
import xyz.avarel.aje.ast.functions.ParameterData;
import xyz.avarel.aje.ast.value.UndefinedNode;
import xyz.avarel.aje.exceptions.SyntaxException;
import xyz.avarel.aje.lexer.Token;
import xyz.avarel.aje.lexer.TokenType;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.AJEParserUtils;
import xyz.avarel.aje.parser.PrefixParser;

import java.util.Collections;
import java.util.List;

public class LambdaFunctionParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        if (!parser.getParserFlags().allowFunctionCreation()) {
            throw new SyntaxException("Function creation are disabled");
        }

        if (parser.match(TokenType.RIGHT_BRACE)) {
            return new FunctionNode(Collections.emptyList(), UndefinedNode.VALUE);
        }

        List<ParameterData> parameters = Collections.emptyList();

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

        if (hasArrow) {
            if (!parser.match(TokenType.ARROW)) {
                parameters = AJEParserUtils.parseParameters(parser);
                parser.eat(TokenType.ARROW);
            }
        } else {
            parameters = Collections.singletonList(new ParameterData("it"));
        }

        Expr expr = parser.parseStatements();

        parser.eat(TokenType.RIGHT_BRACE);

        return new FunctionNode(parameters, expr);
    }
}
