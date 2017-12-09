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

package xyz.avarel.kaiper.parser.parslets.flow

import xyz.avarel.kaiper.ast.expr.Expr
import xyz.avarel.kaiper.ast.expr.tuples.MatchExpr
import xyz.avarel.kaiper.ast.pattern.PatternCase
import xyz.avarel.kaiper.exceptions.SyntaxException
import xyz.avarel.kaiper.lexer.Token
import xyz.avarel.kaiper.lexer.TokenType
import xyz.avarel.kaiper.parser.ExprParser
import xyz.avarel.kaiper.parser.PrefixParser
import java.util.*

class MatchParser : PrefixParser {
    override fun parse(parser: ExprParser, token: Token): Expr {
        val expr = parser.parseExpr()

        val cases = TreeMap<PatternCase, Expr>()

        parser.eat(TokenType.LEFT_BRACE)

        do {
            val position = parser.peek(0).position
            val casePattern = parser.parsePattern()

            if (casePattern.size() != 1) {
                throw SyntaxException("Complex match cases must be in parentheses", position)
            }

            parser.eat(TokenType.ARROW)

            val caseExpr: Expr
            if (parser.nextIs(TokenType.LEFT_BRACE)) {
                caseExpr = parser.parseBlock()
            } else {
                caseExpr = parser.parseExpr()
            }

            if (cases.put(casePattern, caseExpr) != null) {
                throw SyntaxException("Ambiguous match case " + casePattern, position)
            }
        } while (parser.match(TokenType.LINE))

        parser.eat(TokenType.RIGHT_BRACE)

        return MatchExpr(token.position, expr, cases)
    }
}
