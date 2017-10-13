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
import xyz.avarel.kaiper.ast.collections.ArrayNode;
import xyz.avarel.kaiper.ast.collections.DictionaryNode;
import xyz.avarel.kaiper.ast.collections.GetOperation;
import xyz.avarel.kaiper.ast.collections.SetOperation;
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

import java.util.*;

// IN PROGRESS
public class ExprOptimizer implements ExprVisitor<Expr, Void> {
    private static final Set<Class<?>> nodes = new HashSet<>(
            Arrays.asList(BooleanNode.class, DecimalNode.class, IntNode.class, NullNode.class, StringNode.class, Identifier.class)
    );

    @Override
    public Expr visit(Statements expr, Void scope) {
        List<Expr> list = new ArrayList<>(expr.getExprs().size());
        List<Expr> exprs = expr.getExprs();
        for (int i = 0; i < exprs.size() - 1; i++) {
            Expr optimized = optimize(exprs.get(i));
            if (!nodes.contains(optimized.getClass())) list.add(optimized);
        }

        if (exprs.size() >= 1) {
            list.add(optimize(exprs.get(exprs.size() - 1)));
        }

        return new Statements(list);
    }

    @Override
    public Expr visit(FunctionNode expr, Void scope) {
        return new FunctionNode(expr.getPosition(), expr.getPatternCase(), optimize(expr.getExpr()));
    }

    @Override
    public Expr visit(Identifier expr, Void scope) {
        return new Identifier(
                expr.getPosition(),
                expr.getParent() != null ? optimize(expr.getParent()) : null,
                expr.getName()
        );
    }

    @Override
    public Expr visit(Invocation expr, Void scope) {
        return new Invocation(expr.getPosition(), optimize(expr.getLeft()), visit(expr.getArgument(), null));
    }

    @Override
    public Expr visit(BinaryOperation expr, Void scope) {
        Expr left = optimize(expr.getLeft());
        Expr right = optimize(expr.getRight());

        if (left instanceof IntNode) {
            if (right instanceof IntNode) {
                return opInt(((IntNode) left).getValue(), ((IntNode) right).getValue(), expr);
            } else if (right instanceof DecimalNode) {
                return opDecimal(((IntNode) left).getValue(), ((DecimalNode) right).getValue(), expr);
            }
        } else if (left instanceof DecimalNode) {
            if (right instanceof IntNode) {
                return opDecimal(((DecimalNode) left).getValue(), ((IntNode) right).getValue(), expr);
            } else if (right instanceof DecimalNode) {
                return opDecimal(((DecimalNode) left).getValue(), ((DecimalNode) right).getValue(), expr);
            }
        }
        return expr;
    }

    private Expr opInt(int a, int b, BinaryOperation originOp) {
        switch (originOp.getOperator()) {
            case PLUS: return new IntNode(a + b);
            case MINUS: return new IntNode(a - b);
            case TIMES: return new IntNode(a * b);
            case DIVIDE:
                if (b == 0) throw new SyntaxException("Division by 0");
                return new IntNode(a / b);
            case MODULUS: return new IntNode(a % b);
            case POWER: return new IntNode((int) Math.pow(a, b));
            case EQUALS: return BooleanNode.of(a == b);
            case NOT_EQUALS: return BooleanNode.of(a != b);
            case GREATER_THAN: return BooleanNode.of(a > b);
            case GREATER_THAN_EQUAL: return BooleanNode.of(a >= b);
            case LESS_THAN: return BooleanNode.of(a < b);
            case LESS_THAN_EQUAL: return BooleanNode.of(a <= b);
            case SHL: return new IntNode(a << b);
            case SHR: return new IntNode(a >> b);
        }

        return originOp;
    }

    private Expr opDecimal(double a, double b, BinaryOperation originOp) {
        switch (originOp.getOperator()) {
            case PLUS: return new DecimalNode(a + b);
            case MINUS: return new DecimalNode(a - b);
            case TIMES: return new DecimalNode(a * b);
            case DIVIDE: return new DecimalNode(a / b);
            case MODULUS: return new DecimalNode(a % b);
            case POWER: return new DecimalNode((int) Math.pow(a, b));
            case EQUALS: return BooleanNode.of(a == b);
            case NOT_EQUALS: return BooleanNode.of(a != b);
            case GREATER_THAN: return BooleanNode.of(a > b);
            case GREATER_THAN_EQUAL: return BooleanNode.of(a >= b);
            case LESS_THAN: return BooleanNode.of(a < b);
            case LESS_THAN_EQUAL: return BooleanNode.of(a <= b);
        }

        return originOp;
    }

    @Override
    public Expr visit(UnaryOperation expr, Void scope) {
        if (expr.getTarget() instanceof IntNode) {
            if (expr.getOperator() == UnaryOperatorType.MINUS) {
                IntNode target = (IntNode) expr.getTarget();
                return new IntNode(-target.getValue());
            }
        } else if (expr.getTarget() instanceof DecimalNode) {
            if (expr.getOperator() == UnaryOperatorType.MINUS) {
                DecimalNode target = (DecimalNode) expr.getTarget();
                return new DecimalNode(-target.getValue());
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
    public Expr visit(GetOperation expr, Void scope) {
        return new GetOperation(expr.getPosition(), optimize(expr.getLeft()), optimize(expr.getKey()));
    }

    @Override
    public Expr visit(SetOperation expr, Void scope) {
        return new SetOperation(expr.getPosition(), optimize(expr.getLeft()), optimize(expr.getKey()), optimize(expr.getExpr()));
    }

    @Override
    public Expr visit(ReturnExpr expr, Void scope) {
        return new ReturnExpr(expr.getPosition(), optimize(expr.getExpr()));
    }

    @Override
    public Expr visit(ConditionalExpr expr, Void scope) {
        Expr condition = optimize(expr.getCondition());
        Expr ifBranch = optimize(expr.getIfBranch());
        if (condition == BooleanNode.TRUE) {
            return ifBranch;
        }

        Expr elseBranch = expr.getElseBranch() != null ? optimize(expr.getElseBranch()) : NullNode.VALUE;
        if (condition == BooleanNode.FALSE) {
            return elseBranch;
        }
        return new ConditionalExpr(expr.getPosition(), condition, ifBranch, elseBranch);
    }

    @Override
    public Expr visit(ForEachExpr expr, Void scope) {
        return null;
    }

    @Override
    public Expr visit(DictionaryNode expr, Void scope) {
        Map<Expr, Expr> map = new HashMap<>(expr.getMap().size());

        for (Map.Entry<Expr, Expr> entry : expr.getMap().entrySet()) {
            map.put(optimize(entry.getKey()), optimize(entry.getValue()));
        }

        return new DictionaryNode(expr.getPosition(), map);
    }

    @Override
    public Expr visit(NullNode expr, Void scope) {
        return expr;
    }

    @Override
    public Expr visit(IntNode expr, Void scope) {
        return expr;
    }

    @Override
    public Expr visit(DecimalNode expr, Void scope) {
        return expr;
    }

    @Override
    public Expr visit(BooleanNode expr, Void scope) {
        return expr;
    }

    @Override
    public Expr visit(StringNode expr, Void scope) {
        return expr;
    }

    @Override
    public Expr visit(DeclarationExpr expr, Void scope) {
        return new DeclarationExpr(expr.getPosition(), expr.getName(), optimize(expr.getExpr()));
    }

    @Override
    public Expr visit(ModuleNode expr, Void scope) {
        return new ModuleNode(expr.getPosition(), expr.getName(), optimize(expr.getExpr()));
    }

    @Override
    public Expr visit(TypeNode expr, Void scope) {
        throw new UnsupportedOperationException("deprecated");
    }

    @Override
    public Expr visit(WhileExpr expr, Void scope) {
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
