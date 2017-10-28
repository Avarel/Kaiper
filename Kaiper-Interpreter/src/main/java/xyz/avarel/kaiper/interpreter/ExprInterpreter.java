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

import xyz.avarel.kaiper.ast.ExprVisitor;
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
import xyz.avarel.kaiper.ast.pattern.PatternBinder;
import xyz.avarel.kaiper.ast.pattern.PatternCase;
import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.exceptions.InterpreterException;
import xyz.avarel.kaiper.exceptions.ReturnException;
import xyz.avarel.kaiper.runtime.*;
import xyz.avarel.kaiper.runtime.collections.Array;
import xyz.avarel.kaiper.runtime.collections.Dictionary;
import xyz.avarel.kaiper.runtime.collections.Range;
import xyz.avarel.kaiper.runtime.functions.CompiledFunc;
import xyz.avarel.kaiper.runtime.functions.CompiledMultiMethod;
import xyz.avarel.kaiper.runtime.functions.CurriedFunction;
import xyz.avarel.kaiper.runtime.functions.Func;
import xyz.avarel.kaiper.runtime.modules.CompiledModule;
import xyz.avarel.kaiper.runtime.modules.Module;
import xyz.avarel.kaiper.runtime.numbers.Int;
import xyz.avarel.kaiper.runtime.numbers.Number;
import xyz.avarel.kaiper.scope.Scope;

import java.util.List;
import java.util.Map;

//import xyz.avarel.kaiper.ast.expr.operations.SliceOperation;

public class ExprInterpreter implements ExprVisitor<Obj, Scope<String, Obj>> {
    private final VisitorSettings visitorSettings;

    private int recursionDepth = 0;
    private long timeout;

    public ExprInterpreter(VisitorSettings visitorSettings) {
        this.visitorSettings = visitorSettings;
        resetTimeout();
    }

    public VisitorSettings getVisitorSettings() {
        return visitorSettings;
    }

    public void resetTimeout() {
        timeout = System.currentTimeMillis() + visitorSettings.getMsLimit();
    }

    @Override
    public Obj visit(Statements expr, Scope<String, Obj> scope) {
        List<Expr> exprs = expr.getExprs();

        if (exprs.isEmpty()) return Null.VALUE;

        for (int i = 0; i < exprs.size() - 1; i++) {
            resultOf(exprs.get(i), scope);
        }

        return resultOf(exprs.get(exprs.size() - 1), scope);
    }

    // func print(str: String, n: Int) { for (x in 0..n) { print(str) } }
    @Override
    public Obj visit(FunctionNode expr, Scope<String, Obj> scope) {
        if (expr.getName() == null) {
            return new CompiledFunc(expr.getName(), expr.getPatternCase(), expr.getExpr(), this, scope);
        }

        CompiledMultiMethod multiMethod;
        if (scope.getMap().get(expr.getName()) instanceof CompiledMultiMethod) {
            multiMethod = (CompiledMultiMethod) scope.get(expr.getName());
        } else {
            multiMethod = new CompiledMultiMethod(expr.getName());
            declare(scope, expr.getName(), multiMethod);
        }

        if (!multiMethod.addCase(new CompiledFunc(expr.getName(), expr.getPatternCase(), expr.getExpr(), this, scope))) {
            throw new InterpreterException("Ambiguous pattern for function " + multiMethod.getName() + expr.getPosition());
        }

        return multiMethod;
    }

    @Override
    public Obj visit(Identifier expr, Scope<String, Obj> scope) {
        if (expr.getParent() != null) {
            return resultOf(expr.getParent(), scope).getAttr(expr.getName());
        }

        if (scope.contains(expr.getName())) {
            return scope.get(expr.getName());
        }

        throw new InterpreterException(expr.getName() + " is not defined", expr.getPosition());
    }

    @Override
    public Obj visit(Invocation expr, Scope<String, Obj> scope) {
        visitorSettings.checkRecursionDepthLimit(recursionDepth);

        Obj target = resultOf(expr.getLeft(), scope);

        Obj argument = resultOf(expr.getArgument(), scope);

        recursionDepth++;
        Obj result = target.invoke(argument);
        recursionDepth--;

        return result;
    }

    @Override
    public Obj visit(BinaryOperation expr, Scope<String, Obj> scope) {
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

            case REF: // todo handle native functions
                if (right instanceof Func) {
                    return new CurriedFunction((Func) right, left);
                } else {
                    throw new InterpreterException("Illegal currying, right operand is not a function");
                }

            default:
                throw new InterpreterException("Unknown binary operator", expr.getPosition());
        }
    }

    @Override
    public Obj visit(UnaryOperation expr, Scope<String, Obj> scope) {
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
    public Obj visit(ArrayNode expr, Scope<String, Obj> scope) {
        Array array = new Array();

        for (Expr itemExpr : expr.getItems()) {
            Obj item = resultOf(itemExpr, scope);

            if (item instanceof Range) {
                for (Int i : (Range) item) {
                    visitorSettings.checkSizeLimit(array.size());

                    visitorSettings.checkTimeout(timeout);
                    array.add(i);
                }
                continue;
            }

            visitorSettings.checkSizeLimit(array.size());

            array.add(item);
        }

        return array;
    }

//    @Override
//    public Obj visit(SliceOperation expr, Scope<String, Obj> scope) {
//        throw new UnsupportedOperationException("Up for removal");
//    }

    @Override
    public Obj visit(AssignmentExpr expr, Scope<String, Obj> scope) {
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
    public Obj visit(DeclarationExpr expr, Scope<String, Obj> scope) {
        String attr = expr.getName();

        Obj value = resultOf(expr.getExpr(), scope);
        declare(scope, attr, value);
        return Null.VALUE;
    }

    @Override
    public Obj visit(ModuleNode expr, Scope<String, Obj> scope) {
        String name = expr.getName();

        Scope<String, Obj> subScope = scope.subScope();

        resultOf(expr.getExpr(), subScope);

        Module module = new CompiledModule(name, subScope);
        declare(scope, name, module);

        return module;
    }

    // TODO REWORK
    @Override
    public Obj visit(TypeNode expr, Scope<String, Obj> scope) {
        throw new UnsupportedOperationException("TODO");
//        String name = expr.getName();
//
////        CompiledConstructor constructor = new CompiledConstructor(expr.getPatternCase(), expr.getExpr(), this, scope.subPool());
////
////        CompiledType type = new CompiledType(name, constructor);
////
////        declare(scope, name, type);
//
//        return type;
    }

    @Override
    public Obj visit(GetOperation expr, Scope<String, Obj> scope) {
        Obj target = resultOf(expr.getLeft(), scope);
        Obj key = resultOf(expr.getKey(), scope);

        return target.get(key);
    }

    @Override
    public Obj visit(SetOperation expr, Scope<String, Obj> scope) {
        Obj target = resultOf(expr.getLeft(), scope);
        Obj key = resultOf(expr.getKey(), scope);
        Obj value = resultOf(expr.getExpr(), scope);

        return target.set(key, value);
    }

    @Override
    public Obj visit(ReturnExpr expr, Scope<String, Obj> scope) {
        Obj obj = resultOf(expr.getExpr(), scope);

        throw new ReturnException(obj);
    }

    @Override
    public Obj visit(ConditionalExpr expr, Scope<String, Obj> scope) {
        Obj condition = resultOf(expr.getCondition(), scope.subScope());

        if (condition instanceof Bool && condition == Bool.TRUE) {
            return resultOf(expr.getIfBranch(), scope.subScope());
        }
        if (expr.getElseBranch() != null) {
            return resultOf(expr.getElseBranch(), scope.subScope());
        }

        return Null.VALUE;
    }

    @Override
    public Obj visit(WhileExpr expr, Scope<String, Obj> scope) {
        int iteration = 0;
        Expr loopExpr = expr.getAction();

        while (true) {
            visitorSettings.checkIterationLimit(iteration);

            Obj condition = resultOf(expr.getCondition(), scope.subScope());
            if (condition instanceof Bool && condition == Bool.TRUE) {
                Scope<String, Obj> copy = scope.subScope();
                resultOf(loopExpr, copy);
            } else {
                break;
            }

            iteration++;
        }

        return Null.VALUE;
    }

    @Override
    public Obj visit(ForEachExpr expr, Scope<String, Obj> scope) {
        Obj iterableObj = resultOf(expr.getIterable(), scope);

        if (iterableObj instanceof Iterable) {
            Iterable iterable = (Iterable) iterableObj;

            String variant = expr.getVariant();
            Expr loopExpr = expr.getAction();

            int iteration = 0;
            for (Object var : iterable) {
                visitorSettings.checkIterationLimit(iteration);

                if (var instanceof Obj) {
                    Scope<String, Obj> copy = scope.subScope();
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
    public Obj visit(DictionaryNode expr, Scope<String, Obj> scope) {
        Map<Expr, Expr> map = expr.getMap();

        Dictionary dict = new Dictionary();

        for (Map.Entry<Expr, Expr> entry : map.entrySet()) {
            Obj key = resultOf(entry.getKey(), scope);
            Obj value = resultOf(entry.getValue(), scope);
            dict.put(key, value);
        }

        return dict;
    }

    @Override
    public Obj visit(NullNode expr, Scope<String, Obj> scope) {
        return Null.VALUE;
    }

    @Override
    public Obj visit(IntNode expr, Scope<String, Obj> scope) {
        return Int.of(expr.getValue());
    }

    @Override
    public Obj visit(DecimalNode expr, Scope<String, Obj> scope) {
        return Number.of(expr.getValue());
    }

    @Override
    public Obj visit(BooleanNode expr, Scope<String, Obj> scope) {
        if (expr == BooleanNode.TRUE) {
            return Bool.TRUE;
        } else {
            return Bool.FALSE;
        }
    }

    @Override
    public Obj visit(StringNode expr, Scope<String, Obj> scope) {
        return Str.of(expr.getValue());
    }

    @Override
    public Obj visit(TupleExpr expr, Scope<String, Obj> scope) {
        Obj[] values = new Obj[expr.size()];

        List<Expr> elements = expr.getElements();
        for (int i = 0; i < elements.size(); i++) {
            values[i] = resultOf(elements.get(i), scope);
        }

        return new Tuple(values);
    }

    @Override
    public Obj visit(FreeFormStruct expr, Scope<String, Obj> scope) {
        throw new UnsupportedOperationException("in progress");
    }

//    @Override
//    public Obj visit(BindDeclarationExpr expr, Scope<String, Obj> scope) {
//        Obj value = resultOf(expr.getExpr(), scope);
//
//        if (!(value instanceof Tuple)) {
//            value = new Tuple(value);
//        }
//
//        boolean result = new PatternBinder(this, scope).bind(expr.getPatternCase(), value);
//
//        if (!result) {
//            throw new InterpreterException("Could not match (" + value + ") to " + expr.getPatternCase(), expr.getPosition());
//        }
//
//        return Null.VALUE;
//    }
//
//    @Override
//    public Obj visit(BindAssignmentExpr expr, Scope<String, Obj> scope) {
//        throw new InterpreterException("Up for removal", expr.getPosition());
//    }

    @Override
    public Obj visit(MatchExpr expr, Scope<String, Obj> scope) {
        Obj argument = resultOf(expr.getTarget(), scope);

        for (Map.Entry<PatternCase, Expr> entry : expr.getCases().entrySet()) {
            Scope<String, Obj> subScope = scope.subScope();

            if (new PatternBinder(this, subScope).bind(entry.getKey(), argument)) {
                return resultOf(entry.getValue(), subScope);
            }
        }

        throw new InterpreterException("No match cases", expr.getPosition());
    }

    public Obj resultOf(Expr expr, Scope<String, Obj> scope) {
        visitorSettings.checkTimeout(timeout);
        try {
            return expr.accept(this, scope);
        } catch (ComputeException e) {
            throw new InterpreterException(e.getMessage(), expr.getPosition());
        }
    }

    public static boolean assign(Scope<String, Obj> target, String key, Obj value) {
        if (target.getMap().containsKey(key)) {
            target.put(key, value);
            return true;
        } else for (Scope<String, Obj> parent : target.getParents()) {
            if (assign(parent, key, value)) {
                return true;
            }
        }
        return false;
    }

    public static void declare(Scope<String, Obj> target, String key, Obj value) {
        if (target.getMap().containsKey(key)) {
            throw new ComputeException(key + " already exists in the scope");
        }
        target.put(key, value);
    }
}
