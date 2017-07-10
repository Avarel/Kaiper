/*
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package xyz.avarel.aje.interpreter;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.ast.collections.*;
import xyz.avarel.aje.ast.flow.*;
import xyz.avarel.aje.ast.functions.FunctionNode;
import xyz.avarel.aje.ast.functions.ParameterData;
import xyz.avarel.aje.ast.invocation.Invocation;
import xyz.avarel.aje.ast.operations.BinaryOperation;
import xyz.avarel.aje.ast.operations.SliceOperation;
import xyz.avarel.aje.ast.operations.UnaryOperation;
import xyz.avarel.aje.ast.value.*;
import xyz.avarel.aje.ast.variables.AssignmentExpr;
import xyz.avarel.aje.ast.variables.Identifier;
import xyz.avarel.aje.exceptions.ComputeException;
import xyz.avarel.aje.runtime.*;
import xyz.avarel.aje.runtime.collections.Array;
import xyz.avarel.aje.runtime.collections.Dictionary;
import xyz.avarel.aje.runtime.collections.Range;
import xyz.avarel.aje.runtime.functions.CompiledFunc;
import xyz.avarel.aje.runtime.functions.Func;
import xyz.avarel.aje.runtime.functions.Parameter;
import xyz.avarel.aje.runtime.numbers.Decimal;
import xyz.avarel.aje.runtime.numbers.Int;
import xyz.avarel.aje.scope.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExprInterpreter implements ExprVisitor<Obj, Scope> {
    private long timeout = System.currentTimeMillis() + GlobalVisitorSettings.MILLISECONDS_LIMIT;

    public void resetTimeout() {
        timeout = System.currentTimeMillis() + GlobalVisitorSettings.MILLISECONDS_LIMIT;
    }

    @Override
    public Obj visit(Statements statements, Scope scope) {
        List<Expr> exprs = statements.getExprs();

        for (int i = 0; i < exprs.size() - 1; i++) {
            checkTimeout();
            exprs.get(i).accept(this, scope);
        }

        checkTimeout();
        return exprs.get(exprs.size() - 1).accept(this, scope);
    }

    // func print(str: String, n: Int) { for (x in 0..n) { print(str) } }
    @Override
    public Obj visit(FunctionNode expr, Scope scope) {
        List<Parameter> parameters = new ArrayList<>();

        for (ParameterData data : expr.getParameterExprs()) {
            checkTimeout();
            Obj obj_type = data.getTypeExpr().accept(this, scope);

            if (!(obj_type instanceof Type)) {
                throw new ComputeException(obj_type + " is not a valid parameter type");
            }

            parameters.add(Parameter.of(data.getName(), (Type) obj_type, data.getDefault(), data.isRest()));
        }

        checkTimeout();
        Func func = new CompiledFunc(parameters, expr.getExpr(), this, scope.subPool());
        if (expr.getName() != null) scope.declare(expr.getName(), func);
        return func;
    }

    @Override
    public Obj visit(Identifier expr, Scope scope) {
        if (expr.getParent() != null) {
            checkTimeout();
            return expr.getParent().accept(this, scope).getAttr(expr.getName());
        }

        if (scope.contains(expr.getName())) {
            return scope.lookup(expr.getName());
        }

        throw new ComputeException(expr.getName() + " is not defined");
    }

    @Override
    public Obj visit(Invocation expr, Scope scope) {
        checkTimeout();
        Obj target = expr.getLeft().accept(this, scope);

        List<Obj> arguments = new ArrayList<>();

        for (Expr arg : expr.getArguments()) {
            checkTimeout();
            arguments.add(arg.accept(this, scope));
        }

        checkTimeout();
        return target.invoke(arguments);
    }

    @Override
    public Obj visit(BinaryOperation expr, Scope scope) {
        checkTimeout();
        Obj left = expr.getLeft().accept(this, scope);
        checkTimeout();
        Obj right = expr.getRight().accept(this, scope);
        checkTimeout();

        switch (expr.getOperator()) {
            case PLUS:
                return left.plus(right);
            case MINUS:
                return left.minus(right);
            case TIMES:
                return left.times(right);
            case DIVIDE:
                return left.divide(right);
            case MODULUS:
                return left.mod(right);
            case POWER:
                return left.pow(right);

            case EQUALS:
                return left.isEqualTo(right);
            case NOT_EQUALS:
                return left.isEqualTo(right).negate();
            case GREATER_THAN:
                return left.greaterThan(right);
            case LESS_THAN:
                return left.lessThan(right);
            case GREATER_THAN_EQUAL:
                return left.greaterThan(right).or(left.isEqualTo(right));
            case LESS_THAN_EQUAL:
                return left.lessThan(right).or(left.isEqualTo(right));

            case OR:
                return left.or(right);
            case AND:
                return left.and(right);
            default:
                throw new ComputeException("Unknown binary operator");
        }
    }

    @Override
    public Obj visit(UnaryOperation expr, Scope scope) {
        checkTimeout();
        Obj operand = expr.getTarget().accept(this, scope);
        checkTimeout();

        switch (expr.getOperator()) {
            case PLUS:
                return operand;
            case MINUS:
                return operand.negative();
            case NEGATE:
                return operand.negate();
            default:
                throw new ComputeException("Unknown unary operator");
        }
    }

    @Override
    public Obj visit(RangeNode expr, Scope scope) {
        checkTimeout();
        Obj startObj = expr.getLeft().accept(this, scope);
        checkTimeout();
        Obj endObj = expr.getRight().accept(this, scope);

        if (startObj instanceof Int && endObj instanceof Int) {
            int start = ((Int) startObj).value();
            int end = ((Int) endObj).value();

            GlobalVisitorSettings.checkSizeLimit(Math.abs(end - start));

            checkTimeout();
            return new Range(start, end);
        }

        return Undefined.VALUE;
        //throw new ComputeException("Start and end of range must be integers", expr.getPosition());
    }

    @Override
    public Obj visit(ArrayNode expr, Scope scope) {
        Array array = new Array();

        for (Expr itemExpr : expr.getItems()) {
            checkTimeout();
            Obj item = itemExpr.accept(this, scope);

            if (item instanceof Range) {
                for (Int i : (Range) item) {
                    GlobalVisitorSettings.checkSizeLimit(array.size());

                    checkTimeout();
                    array.add(i);
                }
                continue;
            }

            GlobalVisitorSettings.checkSizeLimit(array.size());

            array.add(item);
        }

        return array;
    }

    @Override
    public Obj visit(SliceOperation expr, Scope scope) {
        checkTimeout();
        Obj obj = expr.getLeft().accept(this, scope);
        checkTimeout();
        Obj start = expr.getStart() != null ? expr.getStart().accept(this, scope) : null;
        checkTimeout();
        Obj end = expr.getEnd() != null ? expr.getEnd().accept(this, scope) : null;
        checkTimeout();
        Obj step = expr.getStep() != null ? expr.getStep().accept(this, scope) : null;

        return obj.slice(start, end, step);
    }

    @Override
    public Obj visit(AssignmentExpr expr, Scope scope) {
        String attr = expr.getName();

        if (expr.getParent() != null) {
            checkTimeout();
            Obj target = expr.getParent().accept(this, scope);
            checkTimeout();
            Obj value = expr.getExpr().accept(this, scope);
            return target.setAttr(attr, value);
        }

        if (expr.isDeclaration()) {
            checkTimeout();
            Obj value = expr.getExpr().accept(this, scope);
            scope.declare(attr, value);
            return value;
        } else if (scope.contains(expr.getName())) {
            checkTimeout();
            Obj value = expr.getExpr().accept(this, scope);
            scope.assign(attr, value);
            return value;
        } else {
            throw new ComputeException(expr.getName() + " is not defined");
        }
    }

    @Override
    public Obj visit(GetOperation expr, Scope scope) {
        checkTimeout();
        Obj target = expr.getLeft().accept(this, scope);
        checkTimeout();
        Obj key = expr.getKey().accept(this, scope);

        return target.get(key);
    }

    @Override
    public Obj visit(SetOperation expr, Scope scope) {
        checkTimeout();
        Obj target = expr.getLeft().accept(this, scope);
        checkTimeout();
        Obj key = expr.getKey().accept(this, scope);
        checkTimeout();
        Obj value = expr.getExpr().accept(this, scope);

        return target.set(key, value);
    }

    @Override
    public Obj visit(ReturnExpr expr, Scope scope) {
        checkTimeout();
        Obj obj = expr.getExpr().accept(this, scope);

        throw new ReturnException(obj);
    }

    @Override
    public Obj visit(ConditionalExpr expr, Scope scope) {
        checkTimeout();
        Obj condition = expr.getCondition().accept(this, scope.subPool());

        if (condition instanceof Bool && condition == Bool.TRUE) {
            checkTimeout();
            return expr.getIfBranch().accept(this, scope.subPool());
        }
        if (expr.getElseBranch() != null) {
            checkTimeout();
            return expr.getElseBranch().accept(this, scope.subPool());
        }

        return Undefined.VALUE;
    }

    @Override
    public Obj visit(ForEachExpr expr, Scope scope) {
        checkTimeout();
        Obj iterExpr = expr.getIterable().accept(this, scope);

        if (iterExpr instanceof Iterable) {
            Iterable iterable = (Iterable) iterExpr;

            String variant = expr.getVariant();
            Expr loopExpr = expr.getAction();

            int iter = 0;
            for (Object var : iterable) {
                GlobalVisitorSettings.checkIterationLimit(iter);

                if (var instanceof Obj) {
                    Scope copy = scope.subPool();
                    copy.declare(variant, (Obj) var);
                    checkTimeout();
                    loopExpr.accept(this, copy);
                    iter++;
                } else {
                    throw new ComputeException("Items in iterable do not implement Obj interface");
                }
            }
        }

        return Undefined.VALUE;
    }

    @Override
    public Obj visit(DictionaryNode expr, Scope scope) {
        checkTimeout();
        Map<Expr, Expr> map = expr.getMap();

        Dictionary dict = new Dictionary();

        for (Map.Entry<Expr, Expr> entry : map.entrySet()) {
            checkTimeout();
            Obj key = entry.getKey().accept(this, scope);
            checkTimeout();
            Obj value = entry.getValue().accept(this, scope);
            dict.put(key, value);
        }

        return dict;
    }

    @Override
    public Obj visit(UndefinedNode undefinedNode, Scope scope) {
        return Undefined.VALUE;
    }

    @Override
    public Obj visit(IntNode intNode, Scope scope) {
        return Int.of(intNode.getValue());
    }

    @Override
    public Obj visit(DecimalNode decimalNode, Scope scope) {
        return Decimal.of(decimalNode.getValue());
    }

    @Override
    public Obj visit(BooleanNode booleanNode, Scope scope) {
        switch (booleanNode) {
            case TRUE:
                return Bool.TRUE;
            case FALSE:
            default:
                return Bool.FALSE;
        }
    }

    @Override
    public Obj visit(StringNode stringNode, Scope scope) {
        return Str.of(stringNode.getValue());
    }

    private void checkTimeout() {
        GlobalVisitorSettings.checkTimeout(timeout);
    }
}
