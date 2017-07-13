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

package xyz.avarel.aje.parser.parslets.flow;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.flow.ForEachExpr;
import xyz.avarel.aje.ast.value.UndefinedNode;
import xyz.avarel.aje.exceptions.SyntaxException;
import xyz.avarel.aje.lexer.Token;
import xyz.avarel.aje.lexer.TokenType;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;

public class ForEachParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        if (!parser.getParserFlags().allowControlFlows()) {
            throw new SyntaxException("Control flows are disabled");
        } else if (!parser.getParserFlags().allowLoops()) {
            throw new SyntaxException("Loops are disabled");
        }

        parser.eat(TokenType.LEFT_PAREN);

        String variant = parser.eat(TokenType.IDENTIFIER).getString();

        parser.eatSoftKeyword("in");

        Expr iterable = parser.parseExpr();

        parser.eat(TokenType.RIGHT_PAREN);

        Expr expr;

        if (parser.match(TokenType.LEFT_BRACE)) {
            if (!parser.match(TokenType.RIGHT_BRACE)) {
                expr = parser.parseStatements();
                parser.eat(TokenType.RIGHT_BRACE);
            } else {
                expr = UndefinedNode.VALUE;
            }
        } else {
            expr = parser.parseExpr();
        }


        return new ForEachExpr(variant, iterable, expr);
    }
}
