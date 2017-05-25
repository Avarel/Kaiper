package xyz.avarel.aje.ast;

import xyz.avarel.aje.ast.atoms.*;
import xyz.avarel.aje.ast.invocation.InvocationExpr;
import xyz.avarel.aje.ast.invocation.PipeForwardExpr;
import xyz.avarel.aje.ast.operations.*;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.functions.AJEFunction;
import xyz.avarel.aje.runtime.functions.CompiledFunction;
import xyz.avarel.aje.runtime.lists.Range;
import xyz.avarel.aje.runtime.lists.Vector;
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
        if (expr.getRight() instanceof InvocationExpr) {
            InvocationExpr invocation = ((InvocationExpr) expr.getRight()).copy();
            invocation.getArguments().add(0, expr.getLeft());
            return invocation.accept(this, scope);
        } else if (expr.getRight() instanceof FunctionAtom) {
            FunctionAtom function = (FunctionAtom) expr.getRight();
            return function.accept(this, scope).invoke(expr.getLeft().accept(this, scope));
        } else if (expr.getRight() instanceof NameAtom) {
            NameAtom name = (NameAtom) expr.getRight();
            return name.accept(this, scope).invoke(expr.getLeft().accept(this, scope));
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
        Obj startObj = expr.getLeft().accept(this, scope);
        Obj endObj = expr.getRight().accept(this, scope);

        if (startObj instanceof Int && endObj instanceof Int) {
            return new Range(((Int) startObj).value(), ((Int) endObj).value() - (expr.isExclusive() ? 1: 0));
        }

        return Undefined.VALUE;
    }

    public Obj visit(VectorAtom expr, Scope scope) {
        Vector vector = new Vector();

        for (Expr item : expr.getItems()) {
            if (item instanceof RangeExpr) {
                vector.addAll(((Range) item.accept(this, scope)).toVector());
                continue;
            }
            vector.add(item.accept(this, scope));
        }

        return vector;
    }

    public Obj visit(SliceOperation expr, Scope scope) {
        Obj obj = expr.getLeft().accept(this, scope);

        if (obj instanceof Vector) {
            Vector vector = (Vector) obj;

            Expr startExpr = expr.getStart();
            Expr endExpr = expr.getEnd();
            Expr stepExpr = expr.getStep();

            int start;
            int end;
            int step;

            if (startExpr == null) {
                start = 0;
            } else {
                Obj _obj = startExpr.accept(this, scope);
                if (_obj instanceof Int) {
                    start = ((Int) _obj).value();
                    if (start < 0) {
                        start = vector.size() + start;
                    }
                } else {
                    return Undefined.VALUE;
                }
            }

            if (endExpr == null) {
                end = vector.size();
            } else {
                Obj _obj = endExpr.accept(this, scope);
                if (_obj instanceof Int) {
                    end = ((Int) _obj).value();
                    if (end < 0) {
                        end = vector.size() + end;
                    }
                } else {
                    return Undefined.VALUE;
                }
            }

            if (stepExpr == null) {
                step = 1;
            } else {
                Obj _obj = stepExpr.accept(this, scope);
                if (_obj instanceof Int) {
                    step = ((Int) _obj).value();
                } else {
                    return Undefined.VALUE;
                }
            }

            if (step == 1) {
                return Vector.ofList(vector.subList(start, end));
            } else {
                if (step > 0) {
                    Vector newVector = new Vector();

                    for (int i = start; i < end; i += step) {
                        newVector.add(vector.get(i));
                    }

                    return newVector;
                } else if (step < 0) {
                    Vector newVector = new Vector();

                    for (int i = end - 1; i >= start; i += step) {
                        newVector.add(vector.get(i));
                    }

                    return newVector;
                }
            }
        }

        return Undefined.VALUE;
    }

    public Obj visit(AssignmentExpr expr, Scope scope) {
        scope.assign(expr.getName(), expr.getExpr().accept(this, scope), expr.isDeclaration());
        return Undefined.VALUE;
    }

    public Obj visit(AttributeExpr expr, Scope scope) {
        return expr.getLeft().accept(this, scope).attribute(expr.getName());
    }

    public Obj visit(GetOperation expr, Scope scope) {
        return expr.getLeft().accept(this, scope).get(expr.getArgument().accept(this, scope));
    }
}
