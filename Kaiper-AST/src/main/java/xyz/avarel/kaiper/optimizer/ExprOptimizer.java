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
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.operations.UnaryOperatorType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// IN PROGRESS
public class ExprOptimizer implements ExprVisitor<Expr, Void> {
    @Override
    public Expr visit(Statements expr, Void scope) {
        List<Expr> list = new ArrayList<>(expr.getExprs().size());
        for (Expr statement : expr.getExprs()) {
           list.add(optimize(statement));
        }

        return new Statements(list);
    }

    @Override
    public Expr visit(FunctionNode expr, Void scope) {
        return new FunctionNode(expr.getPosition(), expr.getPatternCase(), optimize(expr.getExpr()));
    }

    @Override
    public Expr visit(Identifier expr, Void scope) {
        return new Identifier(expr.getPosition(), optimize(expr.getParent()), expr.getName());
    }

    @Override
    public Expr visit(Invocation expr, Void scope) {
        return new Invocation(expr.getPosition(), optimize(expr.getLeft()), (TupleExpr) optimize(expr.getArgument()));
    }

    @Override
    public Expr visit(BinaryOperation expr, Void scope) {
        Expr left = optimize(expr.getLeft());
        Expr right = optimize(expr.getRight());

        if ((left instanceof IntNode || left instanceof DecimalNode)
                && (right instanceof IntNode || right instanceof DecimalNode)) {
            boolean endInt = left instanceof IntNode && right instanceof IntNode;
            double leftValue;
            if (left instanceof IntNode) {
                leftValue = ((IntNode) left).getValue();
            } else {
                leftValue = ((DecimalNode) left).getValue();
            }

            double rightValue;
            if (right instanceof IntNode) {
                rightValue = ((IntNode) right).getValue();
            } else {
                rightValue = ((DecimalNode) right).getValue();
            }

            double finalValue;

            switch (expr.getOperator()) {
                case PLUS:
                    finalValue = leftValue + rightValue;
                    break;
                case MINUS:
                    finalValue = leftValue - rightValue;
                    break;
                case TIMES:
                    finalValue = leftValue * rightValue;
                    break;
                case DIVIDE:
                    if (endInt && rightValue == 0) throw new SyntaxException("Division by 0", expr.getPosition());
                    finalValue = leftValue / rightValue;
                    break;
                case MODULUS:
                    finalValue = leftValue % rightValue;
                    break;
                case POWER:
                    finalValue = Math.pow(leftValue, rightValue);
                    break;

                case EQUALS:
                    return BooleanNode.of(leftValue == rightValue);
                case NOT_EQUALS:
                    return BooleanNode.of(leftValue != rightValue);
                case GREATER_THAN:
                    return BooleanNode.of(leftValue > rightValue);
                case GREATER_THAN_EQUAL:
                    return BooleanNode.of(leftValue >= rightValue);
                case LESS_THAN:
                    return BooleanNode.of(leftValue < rightValue);
                case LESS_THAN_EQUAL:
                    return BooleanNode.of(leftValue <= rightValue);

                default: return expr;
            }

            if (endInt) {
                return new IntNode(expr.getPosition(), (int) finalValue);
            } else {
                return new DecimalNode(expr.getPosition(), finalValue);
            }
        }

        return expr;
    }

    @Override
    public Expr visit(UnaryOperation expr, Void scope) {
        if (expr.getTarget() instanceof IntNode) {
            if (expr.getOperator() == UnaryOperatorType.MINUS) {
                IntNode target = (IntNode) expr.getTarget();
                return new IntNode(target.getPosition(), -target.getValue());
            }
        } else if (expr.getTarget() instanceof DecimalNode) {
            if (expr.getOperator() == UnaryOperatorType.MINUS) {
                DecimalNode target = (DecimalNode) expr.getTarget();
                return new DecimalNode(target.getPosition(), -target.getValue());
            }
        } else if (expr.getTarget() instanceof BooleanNode) {
            if (expr.getOperator() == UnaryOperatorType.NEGATE) {
                Expr target = expr.getTarget();
                return target == BooleanNode.TRUE ? BooleanNode.FALSE : BooleanNode.TRUE;
            }
        }
        return expr;
    }

    @Override
    public Expr visit(RangeNode expr, Void scope) {
        throw new UnsupportedOperationException("deprecated");
    }

    @Override
    public Expr visit(ArrayNode expr, Void scope) {
        List<Expr> list = new ArrayList<>(expr.getItems().size());

        for (Expr single : expr.getItems()) {
            list.add(optimize(single));
        }

        return new ArrayNode(expr.getPosition(), list);
    }

    @Override
    public Expr visit(SliceOperation expr, Void scope) {
        throw new UnsupportedOperationException("deprecated");
    }

    @Override
    public Expr visit(AssignmentExpr expr, Void scope) {
        return new AssignmentExpr(expr.getPosition(), optimize(expr.getParent()), expr.getName(), optimize(expr.getExpr()));
    }

    @Override
    public GetOperation visit(GetOperation expr, Void scope) {
        return new GetOperation(expr.getPosition(), optimize(expr.getLeft()), expr.getKey());
    }

    @Override
    public SetOperation visit(SetOperation expr, Void scope) {
        return new SetOperation(expr.getPosition(), optimize(expr.getLeft()), expr.getKey(), optimize(expr.getExpr()));
    }

    @Override
    public ReturnExpr visit(ReturnExpr expr, Void scope) {
        return new ReturnExpr(expr.getPosition(), optimize(expr.getExpr()));
    }

    @Override
    public ConditionalExpr visit(ConditionalExpr expr, Void scope) {
        return null;
    }

    @Override
    public ForEachExpr visit(ForEachExpr expr, Void scope) {
        return null;
    }

    @Override
    public DictionaryNode visit(DictionaryNode expr, Void scope) {
        Map<Expr, Expr> map = new HashMap<>(expr.getMap().size());

        for (Map.Entry<Expr, Expr> entry : expr.getMap().entrySet()) {
            map.put(optimize(entry.getKey()), optimize(entry.getValue()));
        }

        return new DictionaryNode(expr.getPosition(), map);
    }

    @Override
    public NullNode visit(NullNode expr, Void scope) {
        return expr;
    }

    @Override
    public IntNode visit(IntNode expr, Void scope) {
        return expr;
    }

    @Override
    public DecimalNode visit(DecimalNode expr, Void scope) {
        return expr;
    }

    @Override
    public BooleanNode visit(BooleanNode expr, Void scope) {
        return expr;
    }

    @Override
    public StringNode visit(StringNode expr, Void scope) {
        return expr;
    }

    @Override
    public DeclarationExpr visit(DeclarationExpr expr, Void scope) {
        return new DeclarationExpr(expr.getPosition(), expr.getName(), optimize(expr.getExpr()));
    }

    @Override
    public ModuleNode visit(ModuleNode expr, Void scope) {
        return new ModuleNode(expr.getPosition(), expr.getName(), optimize(expr.getExpr()));
    }

    @Override
    public TypeNode visit(TypeNode expr, Void scope) {
        throw new UnsupportedOperationException("deprecated");
    }

    @Override
    public WhileExpr visit(WhileExpr expr, Void scope) {
        throw new UnsupportedOperationException("deprecated");
    }

    @Override
    public TupleExpr visit(TupleExpr expr, Void scope) {
        Map<String, Expr> map = new HashMap<>(expr.getElements().size());

        for (Map.Entry<String, Expr> entry : expr.getElements().entrySet()) {
            map.put(entry.getKey(), optimize(entry.getValue()));
        }

        return new TupleExpr(expr.getPosition(), map);
    }

    @Override
    public Expr visit(BindDeclarationExpr expr, Void scope) {
        throw new UnsupportedOperationException("deprecated");
    }

    @Override
    public Expr visit(BindAssignmentExpr expr, Void scope) {
        throw new UnsupportedOperationException("deprecated");
    }

    private Expr optimize(Expr expr) {
        return expr.accept(this, null);
    }
}
