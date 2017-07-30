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
import xyz.avarel.kaiper.ast.tuples.TupleEntry;
import xyz.avarel.kaiper.ast.tuples.TupleExpr;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.BinaryParser;
import xyz.avarel.kaiper.parser.KaiperParser;

import java.util.ArrayList;
import java.util.List;

public class TupleCommaParser extends BinaryParser {
    public TupleCommaParser() {
        super(Precedence.TUPLE);
    }

    @Override
    public Expr parse(KaiperParser parser, Single left, Token token) {
        List<TupleEntry> entries = new ArrayList<>();
        boolean named;

        if (left instanceof TupleEntry) { // x: 1, __
            TupleEntry tuple = (TupleEntry) left;
            entries.add(tuple);
            named = true;
        } else { // (literal), __
            entries.add(new TupleEntry(left.getPosition(), "_0", left));
            named = false;
        }

        do {
            Single element = parser.parseSingle(getPrecedence());

            if (element instanceof TupleEntry) {
                TupleEntry tuple = (TupleEntry) element;

                entries.add(tuple);
                named = true;
            } else if (named) {
                throw new SyntaxException("Can not mix positional and named tuples", element.getPosition());
            } else {
                entries.add(new TupleEntry(element.getPosition(), "_" + entries.size(), element));
            }
        } while (parser.match(TokenType.COMMA));

        return new TupleExpr(left.getPosition(), entries);
    }
}
