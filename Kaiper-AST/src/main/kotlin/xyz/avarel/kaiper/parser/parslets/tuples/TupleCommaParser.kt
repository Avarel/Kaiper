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

package xyz.avarel.kaiper.parser.parslets.tuples

import xyz.avarel.kaiper.Precedence
import xyz.avarel.kaiper.ast.expr.Expr
import xyz.avarel.kaiper.ast.expr.tuples.TupleExpr
import xyz.avarel.kaiper.exceptions.SyntaxException
import xyz.avarel.kaiper.lexer.Token
import xyz.avarel.kaiper.lexer.TokenType
import xyz.avarel.kaiper.parser.BinaryParser
import xyz.avarel.kaiper.parser.ExprParser
import java.util.*

class TupleCommaParser : BinaryParser(Precedence.TUPLE) {

    override fun parse(parser: ExprParser, left: Expr, token: Token): Expr {
        if (parser.match(TokenType.COMMA)) {
            throw SyntaxException("Illegal expression", token.position)
        }

        val exprs = ArrayList<Expr>()

        exprs.add(left)

        do {
            if (parser.prefixParsers[parser.peek(0).type] == null) {
                break
            }

            exprs.add(parser.parseExpr(Precedence.TUPLE))
        } while (parser.match(TokenType.COMMA))

        return TupleExpr(left.position, exprs)
    }
}