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
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.ast.tuples.TupleExpr;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Position;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.BinaryParser;
import xyz.avarel.kaiper.parser.KaiperParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TupleCommaParser extends BinaryParser {
    public TupleCommaParser() {
        super(Precedence.TUPLE);
    }

    @Override
    public Expr parse(KaiperParser parser, Single left, Token token) {
        List<Single> unnamedElements = new ArrayList<>();
        Map<String, Single> namedElements = new HashMap<>();

        if (left instanceof TupleExpr) { // x: 1, __
            TupleExpr tuple = (TupleExpr) left;

            if (tuple.size() != 1) {
                unnamedElements.add(tuple);
            } else {
                namedElements.putAll(tuple.getNamedElements());
            }

        } else { // (literal), __
            unnamedElements.add(left);
        }

        do {
            Single element = parser.parseSingle(getPrecedence());

            if (element instanceof TupleExpr) {
                TupleExpr tuple = (TupleExpr) element;

                if (tuple.size() != 1) {
                    throw new SyntaxException("Internal error");
                }

                Map.Entry<String, Single> entry = tuple.getNamedElements().entrySet().iterator().next();

                if (namedElements.containsKey(entry.getKey())) {
                    throw duplicateName(entry.getKey(), tuple.getPosition());
                }

                namedElements.put(entry.getKey(), entry.getValue());
            } else {
                if (!namedElements.isEmpty()) {
                    throw new SyntaxException("Can not mix positional and named tuples", element.getPosition());
                }

                unnamedElements.add(element);
            }
        } while (parser.match(TokenType.COMMA));

        return new TupleExpr(left.getPosition(), unnamedElements, namedElements);
    }

    private static SyntaxException duplicateName(String name, Position position) {
        throw new SyntaxException("Duplicate field " + name, position);
    }
}
