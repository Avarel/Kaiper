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
import xyz.avarel.kaiper.ast.expr.flow.ConditionalExpr
import xyz.avarel.kaiper.ast.expr.value.BooleanNode
import xyz.avarel.kaiper.ast.expr.value.NullNode
import xyz.avarel.kaiper.lexer.Token
import xyz.avarel.kaiper.lexer.TokenType
import xyz.avarel.kaiper.parser.ExprParser
import xyz.avarel.kaiper.parser.PrefixParser

class IfElseParser : PrefixParser {
    override fun parse(parser: ExprParser, token: Token): Expr {
        val condition = parser.parseExpr()

        val ifBranch = parser.parseBlock()

        var elseBranch: Expr? = null

        if (parser.matchSignificant(TokenType.ELSE)) {
            elseBranch = if (parser.nextIs(TokenType.IF)) parser.parseExpr() else parser.parseBlock()
        }

        if (condition === BooleanNode.TRUE) {
            return ifBranch
        } else if (condition === BooleanNode.FALSE) {
            return if (elseBranch != null) elseBranch else NullNode.VALUE
        }

        return ConditionalExpr(token.position, condition, ifBranch, elseBranch!!)
    }
}
