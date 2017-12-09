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
import xyz.avarel.kaiper.ast.expr.flow.ForEachExpr
import xyz.avarel.kaiper.lexer.Token
import xyz.avarel.kaiper.lexer.TokenType
import xyz.avarel.kaiper.parser.ExprParser
import xyz.avarel.kaiper.parser.PrefixParser

class ForEachParser : PrefixParser {
    override fun parse(parser: ExprParser, token: Token): Expr {
        val match = parser.match(TokenType.LEFT_PAREN)

        val variant = parser.eat(TokenType.IDENTIFIER).string

        parser.eatSoftKeyword("in")

        val iterable = parser.parseExpr()

        if (match) parser.eat(TokenType.RIGHT_PAREN)

        val expr = parser.parseBlock()

        return ForEachExpr(token.position, variant, iterable, expr)
    }
}
