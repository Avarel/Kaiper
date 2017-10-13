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
import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.tuples.TupleExpr;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.BinaryParser;
import xyz.avarel.kaiper.parser.KaiperParser;

import java.util.HashMap;
import java.util.Map;

public class TupleCommaParser extends BinaryParser {
    public TupleCommaParser() {
        super(Precedence.TUPLE);
    }

    @Override
    public Expr parse(KaiperParser parser, Expr left, Token token) {
        Map<String, Expr> elements = new HashMap<>();

        if (left instanceof TupleExpr) { // x: 1, __
            TupleExpr tuple = (TupleExpr) left;
            elements.putAll(tuple.getElements());
        } else {
            elements.put("value", left);
        }

        do {
            Token name = parser.eat(TokenType.IDENTIFIER);
            parser.eat(TokenType.COLON);
            Expr element = parser.parseExpr(Precedence.INFIX);
            if (elements.put(name.getString(), element) != null) {
                throw new SyntaxException("Duplicate tuple field name", name.getPosition());
            }
        } while (parser.match(TokenType.COMMA));

        return new TupleExpr(left.getPosition(), elements);
    }
}
