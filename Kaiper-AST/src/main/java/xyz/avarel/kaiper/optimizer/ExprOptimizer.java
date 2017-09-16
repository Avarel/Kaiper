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

package xyz.avarel.kaiper.optimizer;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.ModuleNode;
import xyz.avarel.kaiper.ast.TypeNode;
import xyz.avarel.kaiper.ast.collections.*;
import xyz.avarel.kaiper.ast.flow.*;
import xyz.avarel.kaiper.ast.functions.FunctionNode;
import xyz.avarel.kaiper.ast.invocation.Invocation;
import xyz.avarel.kaiper.ast.operations.BinaryOperation;
import xyz.avarel.kaiper.ast.operations.SliceOperation;
import xyz.avarel.kaiper.ast.operations.UnaryOperation;
import xyz.avarel.kaiper.ast.tuples.TupleExpr;
import xyz.avarel.kaiper.ast.value.*;
import xyz.avarel.kaiper.ast.variables.*;


// IN PROGRESS
public class ExprOptimizer implements ExprVisitor<Expr, Void> {
    @Override
    public Expr visit(Statements expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(FunctionNode expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(Identifier expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(Invocation expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(BinaryOperation expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(UnaryOperation expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(RangeNode expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(ArrayNode expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(SliceOperation expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(AssignmentExpr expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(GetOperation expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(SetOperation expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(ReturnExpr expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(ConditionalExpr expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(ForEachExpr expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(DictionaryNode expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(NullNode expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(IntNode expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(DecimalNode expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(BooleanNode expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(StringNode expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(DeclarationExpr expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(ModuleNode expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(TypeNode expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(WhileExpr expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(TupleExpr expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(BindDeclarationExpr expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(BindAssignmentExpr expr, Void scope) {
        return null;
    }
}
