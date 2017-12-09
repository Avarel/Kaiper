/*
 *  Copyright 2017 An Tran and Adrian Todt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package xyz.avarel.kaiper.parser.parslets.variables

import xyz.avarel.kaiper.ast.expr.Expr
import xyz.avarel.kaiper.ast.expr.tuples.MatchExpr
import xyz.avarel.kaiper.ast.expr.value.NullNode
import xyz.avarel.kaiper.ast.expr.variables.DeclarationExpr
import xyz.avarel.kaiper.lexer.Token
import xyz.avarel.kaiper.lexer.TokenType
import xyz.avarel.kaiper.parser.ExprParser
import xyz.avarel.kaiper.parser.PrefixParser
import java.util.*

class DeclarationParser : PrefixParser {
    override fun parse(parser: ExprParser, token: Token): Expr {
        if (parser.match(TokenType.LEFT_PAREN)) {
            val patternCase = parser.parsePattern()

            parser.match(TokenType.RIGHT_PAREN)

            parser.eat(TokenType.ASSIGN)

            val target = parser.parseExpr()

            parser.match(TokenType.LINE)

            val actionsAfter = parser.parseStatements()
            return MatchExpr(token.position, target, Collections.singletonMap(patternCase, actionsAfter))
        }

        val name = parser.eat(TokenType.IDENTIFIER)

        return if (parser.match(TokenType.ASSIGN)) {
            DeclarationExpr(token.position, name.string, parser.parseExpr())
        } else DeclarationExpr(token.position, name.string, NullNode.VALUE)

    }
}
