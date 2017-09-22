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

package xyz.avarel.kaiper.interpreter;

import xyz.avarel.kaiper.ast.*;
import xyz.avarel.kaiper.ast.collections.*;
import xyz.avarel.kaiper.ast.flow.*;
import xyz.avarel.kaiper.ast.functions.FunctionNode;
import xyz.avarel.kaiper.ast.invocation.Invocation;
import xyz.avarel.kaiper.ast.operations.BinaryOperation;
import xyz.avarel.kaiper.ast.operations.SliceOperation;
import xyz.avarel.kaiper.ast.operations.UnaryOperation;
import xyz.avarel.kaiper.ast.pattern.PatternBinder;
import xyz.avarel.kaiper.ast.tuples.TupleExpr;
import xyz.avarel.kaiper.ast.value.*;
import xyz.avarel.kaiper.ast.variables.*;
import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.exceptions.InterpreterException;
import xyz.avarel.kaiper.exceptions.ReturnException;
import xyz.avarel.kaiper.runtime.*;
import xyz.avarel.kaiper.runtime.collections.Array;
import xyz.avarel.kaiper.runtime.collections.Dictionary;
import xyz.avarel.kaiper.runtime.collections.Range;
import xyz.avarel.kaiper.runtime.functions.CompiledFunc;
import xyz.avarel.kaiper.runtime.functions.CompiledMultiMethod;
import xyz.avarel.kaiper.runtime.modules.CompiledModule;
import xyz.avarel.kaiper.runtime.modules.Module;
import xyz.avarel.kaiper.runtime.numbers.Int;
import xyz.avarel.kaiper.runtime.numbers.Number;
import xyz.avarel.kaiper.runtime.types.CompiledConstructor;
import xyz.avarel.kaiper.runtime.types.CompiledType;
import xyz.avarel.kaiper.scope.Scope;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        if (expr.getName() == null) {
            return new CompiledFunc(expr.getName(), expr.getPatternCase(), expr.getExpr(), this, scope);
        }

        CompiledMultiMethod multiMethod;
        if (scope.get(expr.getName()) instanceof CompiledMultiMethod) {
            multiMethod = (CompiledMultiMethod) scope.get(expr.getName());
        } else {
            multiMethod = new CompiledMultiMethod(expr.getName(), this, scope);
            declare(scope, expr.getName(), multiMethod);
        }

        if (multiMethod.getMethodCases().containsKey(expr.getPatternCase())) {
            throw new InterpreterException("Duplicate method definition", expr.getPosition());
        }

        multiMethod.addCase(expr.getPatternCase(), expr.getExpr());

        return multiMethod;
    }

    @Override
    public Obj visit(Identifier expr, Scope scope) {
        if (expr.getParent() != null) {
            return resultOf(expr.getParent(), scope).getAttr(expr.getName());
        }

        if (scope.contains(expr.getName())) {
            return scope.get(expr.getName());
        }

        throw new InterpreterException(expr.getName() + " is not defined", expr.getPosition());
    }

    @Override
    public Obj visit(Invocation expr, Scope scope) {
        GlobalVisitorSettings.checkRecursionDepthLimit(recursionDepth);

        Obj target = resultOf(expr.getLeft(), scope);

        Obj argument = resultOf(expr.getArgument(), scope);

        Tuple tuple = argument instanceof Tuple ? (Tuple) argument : new Tuple(argument);

        recursionDepth++;
        Obj result = target.invoke(tuple);
        recursionDepth--;

        return result;
    }

    @Override
    public Obj visit(BinaryOperation expr, Scope scope) {
        switch (expr.getOperator()) {
            case OR: {
                Obj left = resultOf(expr.getLeft(), scope);
                return left == Bool.TRUE ? Bool.TRUE : resultOf(expr.getRight(), scope);
            }
            case AND: {
                Obj left = resultOf(expr.getLeft(), scope);
                return left == Bool.FALSE ? Bool.FALSE : resultOf(expr.getRight(), scope);
            }
        }

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
                return Bool.of(left.compareTo(right) == 1);
            case LESS_THAN:
                return Bool.of(left.compareTo(right) == -1);
            case GREATER_THAN_EQUAL:
                return Bool.of(left.compareTo(right) >= 0);
            case LESS_THAN_EQUAL:
                return Bool.of(left.compareTo(right) <= 0);

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
        if(!assign(scope, attr, value)) {
            throw new ComputeException(attr + " is not defined, it must be declared first");
        }

        return value;
    }

    @Override
    public Obj visit(DeclarationExpr expr, Scope scope) {
        String attr = expr.getName();

        Obj value = resultOf(expr.getExpr(), scope);
        declare(scope, attr, value);
        return Null.VALUE;
    }

    @Override
    public Obj visit(ModuleNode expr, Scope scope) {
        String name = expr.getName();

        Scope subScope = scope.subPool();

        resultOf(expr.getExpr(), subScope);

        Module module = new CompiledModule(name, subScope);
        declare(scope, name, module);

        return module;
    }

    @Override
    public Obj visit(TypeNode expr, Scope scope) {
        String name = expr.getName();

        CompiledConstructor constructor = new CompiledConstructor(expr.getPatternCase(), expr.getExpr(), this, scope.subPool());

        CompiledType type = new CompiledType(name, constructor);

        declare(scope, name, type);

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
        Obj iterableObj = resultOf(expr.getIterable(), scope);

        if (iterableObj instanceof Iterable) {
            Iterable iterable = (Iterable) iterableObj;

            String variant = expr.getVariant();
            Expr loopExpr = expr.getAction();

            int iteration = 0;
            for (Object var : iterable) {
                GlobalVisitorSettings.checkIterationLimit(iteration);

                if (var instanceof Obj) {
                    Scope copy = scope.subPool();
                    declare(copy, variant, (Obj) var);
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
    public Obj visit(TupleExpr expr, Scope scope) {
        Map<String, Obj> map = new LinkedHashMap<>();

        for (Map.Entry<String, Single> entry : expr.getElements().entrySet()) {
            // confirmed by the compiler
            map.put(entry.getKey(), resultOf(entry.getValue(), scope));
        }

        return new Tuple(map);
    }

    @Override
    public Obj visit(BindDeclarationExpr expr, Scope scope) {
        Obj value = resultOf(expr.getExpr(), scope);

        if (!(value instanceof Tuple)) {
            value = new Tuple(value);
        }

        boolean result = new PatternBinder(expr.getPatternCase(), this, scope).declareFrom((Tuple) value);

        if (!result) {
            throw new InterpreterException("Could not match (" + value + ") to " + expr.getPatternCase(), expr.getPosition());
        }

        return Null.VALUE;
    }

    @Override
    public Obj visit(BindAssignmentExpr expr, Scope scope) {
//        Obj value = resultOf(expr.getExpr(), scope);
//
//        if (!(value instanceof Tuple)) {
//            value = new Tuple(value);
//        }
//
//        boolean result = new PatternBinder(expr.getPatternCase(), this, scope).assignFrom((Tuple) value);
//
//        if (!result) {
            throw new InterpreterException("unsupported", expr.getPosition());
//        }

//        return Null.VALUE;
    }

    public Obj resultOf(Expr expr, Scope scope) {
        checkTimeout();
        try {
            return expr.accept(this, scope);
        } catch (ComputeException e) {
            throw new InterpreterException(e.getMessage(), expr.getPosition());
        }
    }

    public static boolean assign(Scope target, String key, Obj value) {
        if (target.getMap().containsKey(key)) {
            target.put(key, value);
            return true;
        } else for (Scope parent : target.getParents()) {
            if (assign(parent, key, value)) {
                return true;
            }
        }
        return false;
    }

    public static void declare(Scope target, String key, Obj value) {
        if (target.getMap().containsKey(key)) {
            throw new ComputeException(key + " already exists in the scope");
        }
        target.put(key, value);
    }
    
    private void checkTimeout() {
        GlobalVisitorSettings.checkTimeout(timeout);
    }
}
