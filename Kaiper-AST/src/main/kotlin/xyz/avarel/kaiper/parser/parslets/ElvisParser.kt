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

package xyz.avarel.kaiper.parser.parslets

import xyz.avarel.kaiper.Precedence
import xyz.avarel.kaiper.ast.expr.Expr
import xyz.avarel.kaiper.ast.expr.flow.ConditionalExpr
import xyz.avarel.kaiper.ast.expr.operations.BinaryOperation
import xyz.avarel.kaiper.ast.expr.value.NullNode
import xyz.avarel.kaiper.lexer.Token
import xyz.avarel.kaiper.operations.BinaryOperatorType
import xyz.avarel.kaiper.parser.BinaryParser
import xyz.avarel.kaiper.parser.ExprParser

class ElvisParser : BinaryParser(Precedence.INFIX) {

    override fun parse(parser: ExprParser, left: Expr, token: Token): Expr {
        return ConditionalExpr(
                token.position,
                BinaryOperation(
                        token.position,
                        left,
                        NullNode.VALUE,
                        BinaryOperatorType.EQUALS),
                parser.parseExpr(),
                left)
    }
}
