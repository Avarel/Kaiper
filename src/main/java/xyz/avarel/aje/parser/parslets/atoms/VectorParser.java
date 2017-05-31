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

package xyz.avarel.aje.parser.parslets.atoms;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.atoms.VectorAtom;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class VectorParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        List<Expr> exprs = new ArrayList<>();

        if (!parser.match(TokenType.RIGHT_BRACKET)) {
            do {
                exprs.add(parser.parseExpr());
            } while (parser.match(TokenType.COMMA));
            parser.eat(TokenType.RIGHT_BRACKET);
        }

        return new VectorAtom(token.getPosition(), exprs);
    }
}
