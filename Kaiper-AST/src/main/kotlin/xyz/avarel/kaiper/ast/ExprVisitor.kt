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

package xyz.avarel.kaiper.ast

import xyz.avarel.kaiper.ast.expr.ModuleNode
import xyz.avarel.kaiper.ast.expr.TypeNode
import xyz.avarel.kaiper.ast.expr.collections.ArrayNode
import xyz.avarel.kaiper.ast.expr.collections.DictionaryNode
import xyz.avarel.kaiper.ast.expr.collections.GetOperation
import xyz.avarel.kaiper.ast.expr.collections.SetOperation
import xyz.avarel.kaiper.ast.expr.flow.*
import xyz.avarel.kaiper.ast.expr.functions.FunctionNode
import xyz.avarel.kaiper.ast.expr.invocation.Invocation
import xyz.avarel.kaiper.ast.expr.operations.BinaryOperation
import xyz.avarel.kaiper.ast.expr.operations.UnaryOperation
import xyz.avarel.kaiper.ast.expr.tuples.FreeFormStruct
import xyz.avarel.kaiper.ast.expr.tuples.MatchExpr
import xyz.avarel.kaiper.ast.expr.tuples.TupleExpr
import xyz.avarel.kaiper.ast.expr.value.*
import xyz.avarel.kaiper.ast.expr.variables.AssignmentExpr
import xyz.avarel.kaiper.ast.expr.variables.DeclarationExpr
import xyz.avarel.kaiper.ast.expr.variables.Identifier

/**
 * Visitor patterns for [expression][Expr] AST classes.
 * Each [expression][Expr] implements the accept method which
 * is normally `visitor.visit(this, context)`.
 *
 * @param <R> Return type.
 * @param <C> Context type.
 *
 * @see Expr
 *
 * @author Avarel
 */
interface ExprVisitor<out R, in C> {
    fun visit(expr: Statements, context: C): R
    fun visit(expr: FunctionNode, context: C): R
    fun visit(expr: Identifier, context: C): R
    fun visit(expr: Invocation, context: C): R
    fun visit(expr: BinaryOperation, context: C): R
    fun visit(expr: UnaryOperation, context: C): R
    fun visit(expr: ArrayNode, context: C): R
    fun visit(expr: AssignmentExpr, context: C): R
    fun visit(expr: GetOperation, context: C): R
    fun visit(expr: SetOperation, context: C): R
    fun visit(expr: ReturnExpr, context: C): R
    fun visit(expr: ConditionalExpr, context: C): R
    fun visit(expr: ForEachExpr, context: C): R
    fun visit(expr: DictionaryNode, context: C): R
    fun visit(expr: NullNode, context: C): R
    fun visit(expr: IntNode, context: C): R
    fun visit(expr: DecimalNode, context: C): R
    fun visit(expr: BooleanNode, context: C): R
    fun visit(expr: StringNode, context: C): R
    fun visit(expr: DeclarationExpr, context: C): R
    fun visit(expr: ModuleNode, context: C): R
    fun visit(expr: TypeNode, context: C): R
    fun visit(expr: WhileExpr, context: C): R
    fun visit(expr: FreeFormStruct, context: C): R
    fun visit(expr: TupleExpr, context: C): R
    fun visit(expr: MatchExpr, context: C): R
    fun visit(expr: BlockExpr, context: C): R
}
