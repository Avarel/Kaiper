package xyz.avarel.aje.ast;

import xyz.avarel.aje.ast.atoms.*;
import xyz.avarel.aje.ast.invocation.InvocationExpr;
import xyz.avarel.aje.ast.invocation.PipeForwardExpr;
import xyz.avarel.aje.ast.operations.BinaryOperation;
import xyz.avarel.aje.ast.operations.RangeExpr;
import xyz.avarel.aje.ast.operations.SliceExpr;
import xyz.avarel.aje.ast.operations.UnaryOperation;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Slice;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.functions.AJEFunction;
import xyz.avarel.aje.runtime.functions.CompiledFunction;
import xyz.avarel.aje.runtime.numbers.Int;
import xyz.avarel.aje.runtime.pool.Scope;

import java.util.ArrayList;
import java.util.List;

public class ExprVisitor {
    public Obj visit(Statement statement, Scope scope) {
        statement.getBefore().accept(this, scope);
        return statement.getAfter().accept(this,scope);
    }

    public Obj visit(BoolAtom expr, Scope scope) {
        return expr.getValue();
    }

    public Obj visit(FunctionAtom expr, Scope scope) {
        AJEFunction func = new CompiledFunction(expr.getParameters(), expr.getExpr(), scope.subPool());
        if (expr.getName() != null) scope.assign(expr.getName(), func);
        return func;
    }

    public Obj visit(NameAtom expr, Scope scope) {
        return scope.lookup(expr.getName());
    }

    public Obj visit(UndefAtom expr, Scope scope) {
        return expr.getValue();
    }

    public Obj visit(ValueAtom expr, Scope scope) {
        return expr.getValue();
    }

    public Obj visit(InvocationExpr expr, Scope scope) {
        List<Obj> arguments = new ArrayList<>();

        for (Expr arg : expr.getArguments()) {
            arguments.add(arg.accept(this, scope));
        }

        return expr.getLeft().accept(this, scope).invoke(arguments);
    }

    public Obj visit(PipeForwardExpr expr, Scope scope) {
        // DESUGARING
        if (expr.getRight() instanceof InvocationExpr) {
            InvocationExpr invocation = ((InvocationExpr) expr.getRight()).copy();
            invocation.getArguments().add(0, expr.getLeft());
            return invocation.accept(this, scope);
        } else if (expr.getRight() instanceof FunctionAtom) {
            FunctionAtom function = (FunctionAtom) expr.getRight();
            return function.accept(this, scope).invoke(expr.getRight().accept(this, scope));
        } else if (expr.getRight() instanceof NameAtom) {
            NameAtom name = (NameAtom) expr.getRight();
            return name.accept(this, scope).invoke(expr.getRight().accept(this, scope));
        }

        return Undefined.VALUE;
    }

    public Obj visit(BinaryOperation expr, Scope scope) {
        return expr.getOperator().apply(
                expr.getLeft().accept(this, scope),
                expr.getRight().accept(this, scope));
    }

    public Obj visit(UnaryOperation expr, Scope scope) {
        return expr.getOperator().apply(expr.getTarget().accept(this, scope));
    }

    public Obj visit(RangeExpr expr, Scope scope) {
        return expr.getLeft().accept(this, scope).rangeTo(expr.getRight().accept(this, scope));
    }

    public Obj visit(SliceExpr expr, Scope scope) {
        Slice slice = new Slice();

        for (Expr item : expr.getItems()) {
            if (item instanceof RangeExpr) {
                slice.addAll((Slice) item.accept(this, scope));
                continue;
            }
            slice.add(item.accept(this, scope));
        }

        return slice;
    }

    public Obj visit(SublistExpr expr, Scope scope) {
        Obj obj = expr.getLeft().accept(this, scope);
        Obj start = expr.getStart().accept(this, scope);
        Obj end = expr.getEnd().accept(this, scope);

        if (obj instanceof Slice && start instanceof Int && end instanceof Int) {
            Slice obj1 = (Slice) obj;
            Int start1 = (Int) start;
            Int end1 = (Int) end;

            if (start1.value() > end1.value()) {
                if (end1.value() < 0) {
                    end1 = Int.of(Math.floorMod(end1.value(), obj1.size()));
                } else {
                    return Undefined.VALUE;
                }
            }

            List<Obj> sublist = obj1.subList(start1.value(), end1.value());

            return Slice.ofList(sublist);
        }

        return Undefined.VALUE;
    }

    public Obj visit(AssignmentExpr expr, Scope scope) {
        scope.assign(expr.getName(), expr.getExpr().accept(this, scope));
        return Undefined.VALUE;
    }

    public Obj visit(AttributeExpr expr, Scope scope) {
        return expr.getLeft().accept(this, scope).attribute(expr.getName());
    }

    public Obj visit(ListIndexExpr expr, Scope scope) {
        Obj list = expr.getLeft().accept(this, scope);
        Obj index = expr.getIndex().accept(this, scope);

        if (list instanceof Slice && index instanceof Int) {
            return ((Slice) list).get(((Int) index).value());
        }

        return Undefined.VALUE;
    }
}
