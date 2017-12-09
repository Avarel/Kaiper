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

package xyz.avarel.kaiper.ast.expr.variables

import xyz.avarel.kaiper.ast.ExprVisitor
import xyz.avarel.kaiper.ast.expr.Expr
import xyz.avarel.kaiper.lexer.Position

class DeclarationExpr(position: Position, val name: String, val expr: Expr) : Expr(position) {
    override fun <R, C> accept(visitor: ExprVisitor<R, C>, context: C): R {
        return visitor.visit(this, context)
    }
}
