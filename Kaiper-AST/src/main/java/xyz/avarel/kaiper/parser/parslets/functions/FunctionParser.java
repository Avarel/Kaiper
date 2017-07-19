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
import xyz.avarel.kaiper.parser.AJEParser;
import xyz.avarel.kaiper.parser.AJEParserUtils;
import xyz.avarel.kaiper.parser.PrefixParser;

import java.util.List;

public class FunctionParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        if (!parser.getParserFlags().allowFunctionCreation()) {
            throw new SyntaxException("Function creation are disabled");
        }

        String name = null;
        if (parser.match(TokenType.IDENTIFIER)) {
            name = parser.getLast().getString();
        }

        List<ParameterData> parameters = AJEParserUtils.parseParameters(parser);

        Expr expr;

        if (parser.match(TokenType.ASSIGN)) {
            expr = parser.parseExpr();
        } else {
            parser.eat(TokenType.LEFT_BRACE);
            if (parser.match(TokenType.RIGHT_BRACE)) {
                expr = UndefinedNode.VALUE;
            } else {
                expr = parser.parseStatements();
                parser.eat(TokenType.RIGHT_BRACE);
            }
        }

        return new FunctionNode(name, parameters, expr);
    }
}
