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

package xyz.avarel.kaiper.interpreter;

import xyz.avarel.kaiper.ast.*;
import xyz.avarel.kaiper.ast.collections.*;
import xyz.avarel.kaiper.ast.flow.*;
import xyz.avarel.kaiper.ast.functions.FunctionNode;
import xyz.avarel.kaiper.ast.functions.ParameterData;
import xyz.avarel.kaiper.ast.invocation.Invocation;
import xyz.avarel.kaiper.ast.operations.BinaryOperation;
import xyz.avarel.kaiper.ast.operations.SliceOperation;
import xyz.avarel.kaiper.ast.operations.UnaryOperation;
import xyz.avarel.kaiper.ast.tuples.TupleEntry;
import xyz.avarel.kaiper.ast.tuples.TupleExpr;
import xyz.avarel.kaiper.ast.value.*;
import xyz.avarel.kaiper.ast.variables.AssignmentExpr;
import xyz.avarel.kaiper.ast.variables.DeclarationExpr;
import xyz.avarel.kaiper.ast.variables.DestructuringDeclarationExpr;
import xyz.avarel.kaiper.ast.variables.Identifier;
import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.exceptions.InterpreterException;
import xyz.avarel.kaiper.exceptions.ReturnException;
import xyz.avarel.kaiper.runtime.*;
import xyz.avarel.kaiper.runtime.collections.Array;
import xyz.avarel.kaiper.runtime.collections.Dictionary;
import xyz.avarel.kaiper.runtime.collections.Range;
import xyz.avarel.kaiper.runtime.functions.CompiledFunc;
import xyz.avarel.kaiper.runtime.functions.CompiledParameter;
import xyz.avarel.kaiper.runtime.functions.Func;
import xyz.avarel.kaiper.runtime.modules.CompiledModule;
import xyz.avarel.kaiper.runtime.modules.Module;
import xyz.avarel.kaiper.runtime.numbers.Int;
import xyz.avarel.kaiper.runtime.numbers.Number;
import xyz.avarel.kaiper.runtime.types.CompiledConstructor;
import xyz.avarel.kaiper.runtime.types.CompiledType;
import xyz.avarel.kaiper.runtime.types.Type;
import xyz.avarel.kaiper.scope.Scope;

import java.util.*;

public class ExprInterpreter implements ExprVisitor<Obj, Scope> {
    private int recursionDepth = 0;
    private long timeout = System.currentTimeMillis() + GlobalVisitorSettings.MILLISECONDS_LIMIT;

    public void resetTimeout() {
        timeout = System.currentTimeMillis() + GlobalVisitorSettings.MILLISECONDS_LIMIT;
    }

    @Override
    public Obj visit(Statements expr, Scope scope) {
        List<Expr> exprs = expr.getExprs();

        if (exprs.isEmpty()) return Null.VALUE;

        for (int i = 0; i < exprs.size() - 1; i++) {
            resultOf(exprs.get(i), scope);
        }

        return resultOf(exprs.get(exprs.size() - 1), scope);
    }

    // func print(str: String, n: Int) { for (x in 0..n) { print(str) } }
    @Override
    public Obj visit(FunctionNode expr, Scope scope) {
        List<CompiledParameter> parameters = new ArrayList<>();

        for (ParameterData data : expr.getParameterExprs()) {
            parameters.add(new CompiledParameter(data.getName(), data.getDefault(), data.isRest()));
        }

        Func func = new CompiledFunc(expr.getName(), parameters, expr.getExpr(), this, scope.subPool());
        if (expr.getName() != null) scope.declare(expr.getName(), func);
        return func;
    }

    @Override
    public Obj visit(Identifier expr, Scope scope) {
        if (expr.getParent() != null) {
            return resultOf(expr.getParent(), scope).getAttr(expr.getName());
        }

        if (scope.contains(expr.getName())) {
            return scope.lookup(expr.getName());
        }

        throw new InterpreterException(expr.getName() + " is not defined", expr.getPosition());
    }

    @Override
    public Obj visit(Invocation expr, Scope scope) {
        GlobalVisitorSettings.checkRecursionDepthLimit(recursionDepth);

        Obj target = resultOf(expr.getLeft(), scope);

        List<Obj> arguments = new ArrayList<>();

        for (Expr arg : expr.getArguments()) {
            arguments.add(resultOf(arg, scope));
        }


        recursionDepth++;
        Obj result = target.invoke(arguments);
        recursionDepth--;

        return result;
    }

    @Override
    public Obj visit(BinaryOperation expr, Scope scope) {
        Obj left = resultOf(expr.getLeft(), scope);
        Obj right = resultOf(expr.getRight(), scope);

        if (left instanceof Int) {
            if (right instanceof Number) {
                left = Number.of(((Int) left).value());
            }
        } else if (left instanceof Number) {
            if (right instanceof Int) {
                right = Number.of(((Int) right).value());
            }
        }

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

            case SHL:
                return left.shl(right);
            case SHR:
                return left.shr(right);

            default:
                throw new InterpreterException("Unknown binary operator", expr.getPosition());
        }
    }

    @Override
    public Obj visit(UnaryOperation expr, Scope scope) {
        Obj operand = resultOf(expr.getTarget(), scope);

        switch (expr.getOperator()) {
            case PLUS:
                return operand;
            case MINUS:
                return operand.negative();
            case NEGATE:
                return operand.negate();
            default:
                throw new InterpreterException("Unknown unary operator", expr.getPosition());
        }
    }

    @Override
    public Obj visit(RangeNode expr, Scope scope) {
        Obj startObj = resultOf(expr.getLeft(), scope);
        Obj endObj = resultOf(expr.getRight(), scope);

        if (startObj instanceof Int && endObj instanceof Int) {
            int start = ((Int) startObj).value();
            int end = ((Int) endObj).value();

            GlobalVisitorSettings.checkSizeLimit(Math.abs(end - start));

            return new Range(start, end);
        }

        return Null.VALUE;
    }

    @Override
    public Obj visit(ArrayNode expr, Scope scope) {
        Array array = new Array();

        for (Expr itemExpr : expr.getItems()) {
            Obj item = resultOf(itemExpr, scope);

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
        Obj obj = resultOf(expr.getLeft(), scope);
        Obj start = resultOf(expr.getStart(), scope);
        Obj end = resultOf(expr.getEnd(), scope);
        Obj step = resultOf(expr.getStep(), scope);

        return obj.slice(start, end, step);
    }

    @Override
    public Obj visit(AssignmentExpr expr, Scope scope) {
        String attr = expr.getName();

        if (expr.getParent() != null) {
            Obj target = resultOf(expr.getParent(), scope);
            Obj value = resultOf(expr.getExpr(), scope);
            return target.setAttr(attr, value);
        }

        if (!scope.contains(expr.getName())) {
            throw new InterpreterException(expr.getName() + " is not defined", expr.getPosition());
        }

        Obj value = resultOf(expr.getExpr(), scope);
        scope.assign(attr, value);
        return value;
    }

    @Override
    public Obj visit(DeclarationExpr expr, Scope scope) {
        String attr = expr.getName();

        Obj value = resultOf(expr.getExpr(), scope);
        scope.declare(attr, value);
        return Null.VALUE;
    }

    @Override
    public Obj visit(ModuleNode expr, Scope scope) {
        String name = expr.getName();

        Scope subscope = scope.subPool();

        resultOf(expr.getExpr(), subscope);

        Module module = new CompiledModule(name, subscope);
        scope.declare(name, module);

        return module;
    }

    @Override
    public Obj visit(TypeNode expr, Scope scope) {
        String name = expr.getName();

        Scope subscope = scope.subPool();

        Obj superType = resultOf(expr.getSuperType(), scope);
        if (superType != Obj.TYPE && !(superType instanceof CompiledType)) {
            throw new InterpreterException(superType + " can not be extended", expr.getPosition());
        }

        List<CompiledParameter> constructorParameters = new ArrayList<>();
        for (ParameterData data : expr.getParameterExprs()) {
            constructorParameters.add(new CompiledParameter(data.getName(), data.getDefault(), data.isRest()));
        }

        CompiledConstructor constructor = new CompiledConstructor(
                constructorParameters, expr.getSuperParameters(), expr.getExpr(), this, subscope);

        CompiledType type = new CompiledType((Type) superType, name, constructor);

        scope.declare(name, type);

        return type;
    }

    @Override
    public Obj visit(GetOperation expr, Scope scope) {
        Obj target = resultOf(expr.getLeft(), scope);
        Obj key = resultOf(expr.getKey(), scope);

        return target.get(key);
    }

    @Override
    public Obj visit(SetOperation expr, Scope scope) {
        Obj target = resultOf(expr.getLeft(), scope);
        Obj key = resultOf(expr.getKey(), scope);
        Obj value = resultOf(expr.getExpr(), scope);

        return target.set(key, value);
    }

    @Override
    public Obj visit(ReturnExpr expr, Scope scope) {
        Obj obj = resultOf(expr.getExpr(), scope);

        throw new ReturnException(obj);
    }

    @Override
    public Obj visit(ConditionalExpr expr, Scope scope) {
        Obj condition = resultOf(expr.getCondition(), scope.subPool());

        if (condition instanceof Bool && condition == Bool.TRUE) {
            return resultOf(expr.getIfBranch(), scope.subPool());
        }
        if (expr.getElseBranch() != null) {
            return resultOf(expr.getElseBranch(), scope.subPool());
        }

        return Null.VALUE;
    }

    @Override
    public Obj visit(WhileExpr expr, Scope scope) {
        int iteration = 0;
        Expr loopExpr = expr.getAction();

        while (true) {
            GlobalVisitorSettings.checkIterationLimit(iteration);

            Obj condition = resultOf(expr.getCondition(), scope.subPool());
            if (condition instanceof Bool && condition == Bool.TRUE) {
                Scope copy = scope.subPool();
                resultOf(loopExpr, copy);
            } else {
                break;
            }

            iteration++;
        }

        return Null.VALUE;
    }

    @Override
    public Obj visit(ForEachExpr expr, Scope scope) {
        Obj iterExpr = resultOf(expr.getIterable(), scope);

        if (iterExpr instanceof Iterable) {
            Iterable iterable = (Iterable) iterExpr;

            String variant = expr.getVariant();
            Expr loopExpr = expr.getAction();

            int iteration = 0;
            for (Object var : iterable) {
                GlobalVisitorSettings.checkIterationLimit(iteration);

                if (var instanceof Obj) {
                    Scope copy = scope.subPool();
                    copy.declare(variant, (Obj) var);
                    resultOf(loopExpr, copy);
                    iteration++;
                } else {
                    throw new InterpreterException("Items in iterable do not implement Obj interface");
                }
            }
        }

        return Null.VALUE;
    }

    @Override
    public Obj visit(DictionaryNode expr, Scope scope) {
        Map<Single, Single> map = expr.getMap();

        Dictionary dict = new Dictionary();

        for (Map.Entry<Single, Single> entry : map.entrySet()) {
            Obj key = resultOf(entry.getKey(), scope);
            Obj value = resultOf(entry.getValue(), scope);
            dict.put(key, value);
        }

        return dict;
    }

    @Override
    public Obj visit(NullNode expr, Scope scope) {
        return Null.VALUE;
    }

    @Override
    public Obj visit(IntNode expr, Scope scope) {
        return Int.of(expr.getValue());
    }

    @Override
    public Obj visit(DecimalNode expr, Scope scope) {
        return Number.of(expr.getValue());
    }

    @Override
    public Obj visit(BooleanNode expr, Scope scope) {
        if (expr == BooleanNode.TRUE) {
            return Bool.TRUE;
        } else {
            return Bool.FALSE;
        }
    }

    @Override
    public Obj visit(StringNode expr, Scope scope) {
        return Str.of(expr.getValue());
    }

    @Override
    public Obj visit(TupleEntry expr, Scope scope) {
        Obj value = resultOf(expr.getExpr(), scope);
        return new Tuple(Collections.singletonMap(expr.getName(), value));
    }

    @Override
    public Obj visit(TupleExpr expr, Scope scope) {
        Map<String, Obj> map = new LinkedHashMap<>();

        for (TupleEntry entry : expr.getEntries()) {
            Obj value = resultOf(entry.getExpr(), scope);
            map.put(entry.getName(), value);
        }

        return new Tuple(map);
    }

    @Override
    public Obj visit(DestructuringDeclarationExpr expr, Scope scope) {
        Obj value = resultOf(expr.getExpr(), scope);

        if (!(value instanceof Tuple)) {
            value = new Tuple(Collections.singletonMap("_0", value));
        }

        boolean result = PatternBinder.bind(this, expr.getPatternCase(), (Tuple) value, scope);

        if (!result) {
            throw new InterpreterException("Could not match (" + value + ") to " + expr.getPatternCase(), expr.getPosition());
        }

        return Null.VALUE;
    }

    private Obj resultOf(Expr expr, Scope scope) {
        checkTimeout();
        try {
            return expr.accept(this, scope);
        } catch (ComputeException e) {
            throw new InterpreterException(e.getMessage(), expr.getPosition());
        }
    }

    private void checkTimeout() {
        GlobalVisitorSettings.checkTimeout(timeout);
    }
}
