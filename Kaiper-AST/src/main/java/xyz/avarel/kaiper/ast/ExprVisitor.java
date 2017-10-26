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

package xyz.avarel.kaiper.ast;

import xyz.avarel.kaiper.ast.expr.Expr;
import xyz.avarel.kaiper.ast.expr.ModuleNode;
import xyz.avarel.kaiper.ast.expr.TypeNode;
import xyz.avarel.kaiper.ast.expr.collections.ArrayNode;
import xyz.avarel.kaiper.ast.expr.collections.DictionaryNode;
import xyz.avarel.kaiper.ast.expr.collections.GetOperation;
import xyz.avarel.kaiper.ast.expr.collections.SetOperation;
import xyz.avarel.kaiper.ast.expr.flow.*;
import xyz.avarel.kaiper.ast.expr.functions.FunctionNode;
import xyz.avarel.kaiper.ast.expr.invocation.Invocation;
import xyz.avarel.kaiper.ast.expr.operations.BinaryOperation;
import xyz.avarel.kaiper.ast.expr.operations.UnaryOperation;
import xyz.avarel.kaiper.ast.expr.tuples.FreeFormStruct;
import xyz.avarel.kaiper.ast.expr.tuples.MatchExpr;
import xyz.avarel.kaiper.ast.expr.tuples.TupleExpr;
import xyz.avarel.kaiper.ast.expr.value.*;
import xyz.avarel.kaiper.ast.expr.variables.AssignmentExpr;
import xyz.avarel.kaiper.ast.expr.variables.DeclarationExpr;
import xyz.avarel.kaiper.ast.expr.variables.Identifier;

/**
 * Visitor patterns for {@link Expr expression} AST classes.
 * Each {@link Expr expression} implements the accept method which
 * is normally {@code visitor.visit(this, context)}.
 *
 * @param <R> Return type.
 * @param <C> Context type.
 *
 * @see Expr
 * @author Avarel
 */
public interface ExprVisitor<R, C> {
    R visit(Statements expr, C context);

    R visit(FunctionNode expr, C context);

    R visit(Identifier expr, C context);

    R visit(Invocation expr, C context);

    R visit(BinaryOperation expr, C context);

    R visit(UnaryOperation expr, C context);

    R visit(ArrayNode expr, C context);

//    @Deprecated
//    R visit(SliceOperation expr, C context);

    R visit(AssignmentExpr expr, C context);

    R visit(GetOperation expr, C context);

    R visit(SetOperation expr, C context);

    R visit(ReturnExpr expr, C context);

    R visit(ConditionalExpr expr, C context);

    R visit(ForEachExpr expr, C context);

    R visit(DictionaryNode expr, C context);

    R visit(NullNode expr, C context);

    R visit(IntNode expr, C context);

    R visit(DecimalNode expr, C context);

    R visit(BooleanNode expr, C context);

    R visit(StringNode expr, C context);

    R visit(DeclarationExpr expr, C context);

    R visit(ModuleNode expr, C context);

    R visit(TypeNode expr, C context);

    R visit(WhileExpr expr, C context);

    R visit(FreeFormStruct expr, C context);

//    R visit(BindDeclarationExpr expr, C context);

//    @Deprecated
//    R visit(BindAssignmentExpr expr, C context);

    R visit(TupleExpr expr, C context);

    R visit(MatchExpr expr, C context);
}
