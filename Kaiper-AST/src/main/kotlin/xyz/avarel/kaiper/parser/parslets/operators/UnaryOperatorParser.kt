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

package xyz.avarel.kaiper.parser.parslets.operators

import xyz.avarel.kaiper.Precedence
import xyz.avarel.kaiper.ast.expr.Expr
import xyz.avarel.kaiper.ast.expr.operations.UnaryOperation
import xyz.avarel.kaiper.ast.expr.value.BooleanNode
import xyz.avarel.kaiper.ast.expr.value.DecimalNode
import xyz.avarel.kaiper.ast.expr.value.IntNode
import xyz.avarel.kaiper.lexer.Token
import xyz.avarel.kaiper.operations.UnaryOperatorType
import xyz.avarel.kaiper.parser.ExprParser
import xyz.avarel.kaiper.parser.PrefixParser

class UnaryOperatorParser(private val operator: UnaryOperatorType) : PrefixParser {

    override fun parse(parser: ExprParser, token: Token): Expr {
        val left = parser.parseExpr(Precedence.PREFIX)

        if (left is IntNode) {
            if (operator == UnaryOperatorType.MINUS) {
                return IntNode(-left.value)
            }
        } else if (left is DecimalNode) {
            if (operator == UnaryOperatorType.MINUS) {
                return DecimalNode(-left.value)
            }
        } else if (left is BooleanNode) {
            if (operator == UnaryOperatorType.NEGATE) {
                return if (left === BooleanNode.TRUE) BooleanNode.FALSE else BooleanNode.TRUE
            }
        }

        return UnaryOperation(token.position, left, operator)
    }
}
