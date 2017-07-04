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
import xyz.avarel.aje.ast.ValueNode;
import xyz.avarel.aje.ast.flow.ReturnExpr;
import xyz.avarel.aje.exceptions.SyntaxException;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.Undefined;

public class ReturnParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        if (!parser.getParserFlags().allowControlFlows()) {
            throw new SyntaxException("Control flows are disabled");
        }

        Expr expr;
        if (parser.peekAny(TokenType.LINE, TokenType.SEMICOLON, TokenType.RIGHT_BRACE)) {
            expr = new ValueNode(Undefined.VALUE);
        } else {
            expr = parser.parseExpr();
        }
        return new ReturnExpr(expr);
    }
}