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

package xyz.avarel.kaiper.parser.parslets.functional

import xyz.avarel.kaiper.Precedence
import xyz.avarel.kaiper.ast.expr.Expr
import xyz.avarel.kaiper.ast.expr.invocation.Invocation
import xyz.avarel.kaiper.ast.expr.operations.BinaryOperation
import xyz.avarel.kaiper.lexer.Token
import xyz.avarel.kaiper.lexer.TokenType
import xyz.avarel.kaiper.parser.BinaryParser
import xyz.avarel.kaiper.parser.ExprParser
import java.util.*

class InvocationParser : BinaryParser(Precedence.POSTFIX) {
    override fun parse(parser: ExprParser, left: Expr, token: Token): Expr {
        var leftVar = left
        val arguments = ArrayList<Expr>()

        if (leftVar is BinaryOperation) {
            val operation = leftVar
            arguments.add(operation.left)
            leftVar = operation.right
        }

        if (!parser.match(TokenType.RIGHT_PAREN)) {
            do {
                arguments.add(parser.parseExpr(Precedence.TUPLE))
            } while (parser.match(TokenType.COMMA))
            parser.eat(TokenType.RIGHT_PAREN)
        }

        return Invocation(token.position, leftVar, arguments)
    }
}
