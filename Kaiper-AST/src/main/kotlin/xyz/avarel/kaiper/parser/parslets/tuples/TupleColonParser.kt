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
import xyz.avarel.kaiper.ast.expr.tuples.FreeFormStruct
import xyz.avarel.kaiper.ast.expr.variables.Identifier
import xyz.avarel.kaiper.exceptions.SyntaxException
import xyz.avarel.kaiper.lexer.Token
import xyz.avarel.kaiper.lexer.TokenType
import xyz.avarel.kaiper.parser.BinaryParser
import xyz.avarel.kaiper.parser.ExprParser
import java.util.*

class TupleColonParser : BinaryParser(Precedence.FREEFORM_STRUCT) {

    override fun parse(parser: ExprParser, left: Expr, token: Token): Expr {
        if (left !is Identifier || left.parent != null) {
            throw SyntaxException("Tuple entry names must be simple names", left.position)
        }

        val elements = LinkedHashMap<String, Expr>()

        val value = parser.parseExpr(precedence)

        elements.put(left.name, value)

        do {
            val name = parser.eat(TokenType.IDENTIFIER)
            parser.eat(TokenType.COLON)
            val element = parser.parseExpr(Precedence.INFIX)
            if (elements.put(name.string, element) != null) {
                throw SyntaxException("Duplicate tuple field name", name.position)
            }
        } while (parser.match(TokenType.COMMA))

        return FreeFormStruct(left.position, elements)
    }
}
