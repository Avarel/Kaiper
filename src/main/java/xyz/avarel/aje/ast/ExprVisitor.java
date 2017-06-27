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

package xyz.avarel.aje.ast;

import xyz.avarel.aje.ast.collections.*;
import xyz.avarel.aje.ast.flow.*;
import xyz.avarel.aje.ast.functions.FunctionNode;
import xyz.avarel.aje.ast.functions.ParameterData;
import xyz.avarel.aje.ast.invocation.Invocation;
import xyz.avarel.aje.ast.operations.BinaryOperation;
import xyz.avarel.aje.ast.operations.SliceOperation;
import xyz.avarel.aje.ast.operations.UnaryOperation;
import xyz.avarel.aje.ast.variables.AssignmentExpr;
import xyz.avarel.aje.ast.variables.Identifier;
import xyz.avarel.aje.exceptions.ComputeException;
import xyz.avarel.aje.runtime.Bool;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Prototype;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.collections.Dictionary;
import xyz.avarel.aje.runtime.collections.Range;
import xyz.avarel.aje.runtime.collections.Vector;
import xyz.avarel.aje.runtime.functions.CompiledFunc;
import xyz.avarel.aje.runtime.functions.Func;
import xyz.avarel.aje.runtime.functions.Parameter;
import xyz.avarel.aje.runtime.numbers.Int;
import xyz.avarel.aje.scope.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExprVisitor {
    public Obj visit(Statements statements, Scope scope) {
        List<Expr> exprs = statements.getExprs();

        for (int i = 0; i < exprs.size() - 1; i++) {
            exprs.get(i).accept(this, scope);
        }

        return exprs.get(exprs.size() - 1).accept(this, scope);
    }

    // func print(str: String, n: Int) { for (x in 0..n) { print(str) } }
    public Obj visit(FunctionNode expr, Scope scope) {
        List<Parameter> parameters = new ArrayList<>();

        for (ParameterData data : expr.getParameterExprs()) {
            Obj obj_type = data.getTypeExpr().accept(this, scope);

            if (!(obj_type instanceof Prototype)) {
                throw new ComputeException(obj_type + " is not a valid parameter type", data.getTypeExpr().getPosition());
            }

            parameters.add(Parameter.of(data.getName(), (Prototype) obj_type, data.getDefault()));
        }

        Func func = new CompiledFunc(parameters, expr.getExpr(), scope.subPool());
        if (expr.getName() != null) scope.declare(expr.getName(), func);
        return func;
    }

    public Obj visit(Identifier expr, Scope scope) {
        if (expr.getParent() != null) {
            return expr.getParent().accept(this, scope).getAttr(expr.getName());
        }

        if (scope.contains(expr.getName())) {
            return scope.lookup(expr.getName());
        }

        throw new ComputeException(expr.getName() + " is not defined", expr.getPosition());
    }

    public Obj visit(ValueNode expr, Scope scope) {
        return expr.getValue();
    }

    public Obj visit(Invocation expr, Scope scope) {
        Obj target = expr.getLeft().accept(this, scope);

        List<Obj> arguments = new ArrayList<>();

        for (Expr arg : expr.getArguments()) {
            arguments.add(arg.accept(this, scope));
        }

        return target.invoke(arguments);
    }

    public Obj visit(BinaryOperation expr, Scope scope) {
        Obj left = expr.getLeft().accept(this, scope);
        Obj right = expr.getRight().accept(this, scope);

        return expr.getOperator().apply(left, right);
    }

    public Obj visit(UnaryOperation expr, Scope scope) {
        Obj operand = expr.getTarget().accept(this, scope);

        return expr.getOperator().apply(operand);
    }

    public Obj visit(RangeNode expr, Scope scope) {
        Obj startObj = expr.getLeft().accept(this, scope);
        Obj endObj = expr.getRight().accept(this, scope);

        if (startObj instanceof Int && endObj instanceof Int) {
            int start = ((Int) startObj).value();
            int end = ((Int) endObj).value();

            return new Range(start, end);
        }

        return Undefined.VALUE;
        //throw new ComputeException("Start and end of range must be integers", expr.getPosition());
    }

    public Obj visit(VectorNode expr, Scope scope) {
        Vector vector = new Vector();

        for (Expr itemExpr : expr.getItems()) {
            Obj item = itemExpr.accept(this, scope);

            vector.add(item);
        }

        return vector;
    }

    public Obj visit(SliceOperation expr, Scope scope) {
        Obj obj = expr.getLeft().accept(this, scope);

        Obj start = expr.getStart() != null ? expr.getStart().accept(this, scope) : null;
        Obj end = expr.getEnd() != null ? expr.getEnd().accept(this, scope) : null;
        Obj step = expr.getStep() != null ? expr.getStep().accept(this, scope) : null;

        return obj.slice(start, end, step);
    }

    public Obj visit(AssignmentExpr expr, Scope scope) {
        String attr = expr.getName();

        if (expr.getParent() != null) {
            Obj target = expr.getParent().accept(this, scope);
            Obj value = expr.getExpr().accept(this, scope);
            return target.setAttr(attr, value);
        }

        if (expr.isDeclaration()) {
            Obj value = expr.getExpr().accept(this, scope);
            scope.declare(attr, value);
            return value;
        } else if (scope.contains(expr.getName())) {
            Obj value = expr.getExpr().accept(this, scope);
            scope.assign(attr, value);
            return value;
        } else {
            throw new ComputeException(expr.getName() + " is not defined", expr.getPosition());
        }
    }

    public Obj visit(GetOperation expr, Scope scope) {
        Obj target = expr.getLeft().accept(this, scope);
        Obj key = expr.getKey().accept(this, scope);

        return target.get(key);
    }

    public Obj visit(SetOperation expr, Scope scope) {
        Obj target = expr.getLeft().accept(this, scope);
        Obj key = expr.getKey().accept(this, scope);
        Obj value = expr.getExpr().accept(this, scope);

        return target.set(key, value);
    }

    public Obj visit(ReturnExpr expr, Scope scope) {
        Obj obj = expr.getExpr().accept(this, scope);

        throw new ReturnException(expr.getPosition(), obj);
    }

    public Obj visit(ConditionalExpr expr, Scope scope) {
        Obj condition = expr.getCondition().accept(this, scope.subPool());

        if (condition instanceof Bool && condition == Bool.TRUE) {
            return expr.getIfBranch().accept(this, scope.subPool());
        }
        if (expr.getElseBranch() != null) {
            return expr.getElseBranch().accept(this, scope.subPool());
        }

        return Undefined.VALUE;
    }

    public Obj visit(ForEachExpr expr, Scope scope) {
        Obj iterExpr = expr.getIterable().accept(this, scope);

        if (iterExpr instanceof Iterable) {
            Iterable iterable = (Iterable) iterExpr;

            String variant = expr.getVariant();
            Expr loopExpr = expr.getAction();

            for (Object var : iterable) {
                if (var instanceof Obj) {
                    Scope copy = scope.subPool();
                    copy.declare(variant, (Obj) var);
                    loopExpr.accept(this, copy);
                } else {
                    throw new ComputeException("Items in iterable do not implement Obj interface", expr.getIterable().getPosition());
                }
            }
        }

        return Undefined.VALUE;
    }

    public Obj visit(DictionaryNode expr, Scope scope) {
        Map<Expr, Expr> map = expr.getMap();

        Dictionary dict = new Dictionary();

        for(Map.Entry<Expr, Expr> entry : map.entrySet()) {
            dict.put(
                    entry.getKey().accept(this, scope),
                    entry.getValue().accept(this, scope));
        }

        return dict;
    }
}
