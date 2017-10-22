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

package xyz.avarel.kaiper.parser.parslets.tuples;

import xyz.avarel.kaiper.Precedence;
import xyz.avarel.kaiper.ast.expr.Expr;
import xyz.avarel.kaiper.ast.expr.tuples.FreeFormStruct;
import xyz.avarel.kaiper.ast.expr.variables.Identifier;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.BinaryParser;
import xyz.avarel.kaiper.parser.ExprParser;

import java.util.LinkedHashMap;
import java.util.Map;

public class TupleColonParser extends BinaryParser {
    public TupleColonParser() {
        super(Precedence.FREEFORM_STRUCT);
    }

    @Override
    public Expr parse(ExprParser parser, Expr left, Token token) {
        if (!(left instanceof Identifier) || ((Identifier) left).getParent() != null) {
            throw new SyntaxException("Tuple entry names must be simple names", left.getPosition());
        }

        Map<String, Expr> elements = new LinkedHashMap<>();

        Expr value = parser.parseExpr(getPrecedence());

        elements.put(((Identifier) left).getName(), value);

        do {
            Token name = parser.eat(TokenType.IDENTIFIER);
            parser.eat(TokenType.COLON);
            Expr element = parser.parseExpr(Precedence.INFIX);
            if (elements.put(name.getString(), element) != null) {
                throw new SyntaxException("Duplicate tuple field name", name.getPosition());
            }
        } while (parser.match(TokenType.COMMA));

        return new FreeFormStruct(left.getPosition(), elements);
    }
}
