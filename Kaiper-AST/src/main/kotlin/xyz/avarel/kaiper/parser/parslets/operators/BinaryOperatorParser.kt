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

import xyz.avarel.kaiper.ast.expr.Expr
import xyz.avarel.kaiper.ast.expr.operations.BinaryOperation
import xyz.avarel.kaiper.ast.expr.value.BooleanNode
import xyz.avarel.kaiper.ast.expr.value.DecimalNode
import xyz.avarel.kaiper.ast.expr.value.IntNode
import xyz.avarel.kaiper.exceptions.SyntaxException
import xyz.avarel.kaiper.lexer.Position
import xyz.avarel.kaiper.lexer.Token
import xyz.avarel.kaiper.operations.BinaryOperatorType
import xyz.avarel.kaiper.parser.BinaryParser
import xyz.avarel.kaiper.parser.ExprParser

class BinaryOperatorParser(precedence: Int, leftAssoc: Boolean, private val operator: BinaryOperatorType) : BinaryParser(precedence, leftAssoc) {
    override fun parse(parser: ExprParser, left: Expr, token: Token): Expr {
        val right = parser.parseExpr(precedence - if (isLeftAssoc) 0 else 1)

        when (left) {
            is IntNode -> when (right) {
                is IntNode -> return opInt(token.position, left.value, right.value)
                is DecimalNode -> return opDecimal(token.position, left.value.toDouble(), right.value)
            }
            is DecimalNode -> when (right) {
                is IntNode -> return opDecimal(token.position, left.value, right.value.toDouble())
                is DecimalNode -> return opDecimal(token.position, left.value, right.value)
            }
        }

        return BinaryOperation(token.position, left, right, operator)
    }

    private fun opInt(position: Position, a: Int, b: Int): Expr {
        return when (operator) {
            BinaryOperatorType.PLUS -> IntNode(a + b)
            BinaryOperatorType.MINUS -> IntNode(a - b)
            BinaryOperatorType.TIMES -> IntNode(a * b)
            BinaryOperatorType.DIVIDE -> {
                if (b == 0) throw SyntaxException("Division by 0", position)
                IntNode(a / b)
            }
            BinaryOperatorType.MODULUS -> IntNode(a % b)
            BinaryOperatorType.POWER -> IntNode(Math.pow(a.toDouble(), b.toDouble()).toInt())
            BinaryOperatorType.EQUALS -> BooleanNode.of(a == b)
            BinaryOperatorType.NOT_EQUALS -> BooleanNode.of(a != b)
            BinaryOperatorType.GREATER_THAN -> BooleanNode.of(a > b)
            BinaryOperatorType.GREATER_THAN_EQUAL -> BooleanNode.of(a >= b)
            BinaryOperatorType.LESS_THAN -> BooleanNode.of(a < b)
            BinaryOperatorType.LESS_THAN_EQUAL -> BooleanNode.of(a <= b)
            BinaryOperatorType.SHL -> IntNode(a shl b)
            BinaryOperatorType.SHR -> IntNode(a shr b)
            else -> BinaryOperation(position, IntNode(a), IntNode(b), operator)
        }
    }

    private fun opDecimal(position: Position, a: Double, b: Double): Expr {
        return when (operator) {
            BinaryOperatorType.PLUS -> DecimalNode(a + b)
            BinaryOperatorType.MINUS -> DecimalNode(a - b)
            BinaryOperatorType.TIMES -> DecimalNode(a * b)
            BinaryOperatorType.DIVIDE -> DecimalNode(a / b)
            BinaryOperatorType.MODULUS -> DecimalNode(a % b)
            BinaryOperatorType.POWER -> DecimalNode(Math.pow(a, b).toInt().toDouble())
            BinaryOperatorType.EQUALS -> BooleanNode.of(a == b)
            BinaryOperatorType.NOT_EQUALS -> BooleanNode.of(a != b)
            BinaryOperatorType.GREATER_THAN -> BooleanNode.of(a > b)
            BinaryOperatorType.GREATER_THAN_EQUAL -> BooleanNode.of(a >= b)
            BinaryOperatorType.LESS_THAN -> BooleanNode.of(a < b)
            BinaryOperatorType.LESS_THAN_EQUAL -> BooleanNode.of(a <= b)
            else -> BinaryOperation(position, DecimalNode(a), DecimalNode(b), operator)
        }
    }
}