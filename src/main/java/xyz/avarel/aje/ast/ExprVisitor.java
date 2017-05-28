package xyz.avarel.aje.ast;

import xyz.avarel.aje.ast.atoms.FunctionAtom;
import xyz.avarel.aje.ast.atoms.RangeExpr;
import xyz.avarel.aje.ast.atoms.ValueAtom;
import xyz.avarel.aje.ast.atoms.VectorAtom;
import xyz.avarel.aje.ast.invocation.InvocationExpr;
import xyz.avarel.aje.ast.operations.BinaryOperation;
import xyz.avarel.aje.ast.operations.GetOperation;
import xyz.avarel.aje.ast.operations.SliceOperation;
import xyz.avarel.aje.ast.operations.UnaryOperation;
import xyz.avarel.aje.ast.variables.AssignmentExpr;
import xyz.avarel.aje.ast.variables.NameAtom;
import xyz.avarel.aje.exceptions.ComputeException;
import xyz.avarel.aje.runtime.Bool;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.functions.AJEFunction;
import xyz.avarel.aje.runtime.functions.CompiledFunction;
import xyz.avarel.aje.runtime.functions.ReturnException;
import xyz.avarel.aje.runtime.lists.Range;
import xyz.avarel.aje.runtime.lists.Vector;
import xyz.avarel.aje.runtime.numbers.Int;
import xyz.avarel.aje.scope.Scope;

import java.util.ArrayList;
import java.util.List;

public class ExprVisitor {
    public Obj visit(Statements statements, Scope scope) {
        statements.getBefore().accept(this, scope);
        return statements.getAfter().accept(this, scope);
    }

    public Obj visit(FunctionAtom expr, Scope scope) {
        AJEFunction func = new CompiledFunction(expr.getParameters(), expr.getExpr(), scope.subPool());
        if (expr.getName() != null) scope.declare(expr.getName(), func);
        return func;
    }

    public Obj visit(NameAtom expr, Scope scope) {
        if (expr.getFrom() != null) {
            return expr.getFrom().accept(this, scope).getAttr(expr.getName());
        }

        Obj value = scope.lookup(expr.getName());
        if (value != null) {
            return value;
        } else {
            throw new ComputeException(expr.getName() + " is not defined", expr.getPosition());
        }
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
            int start = ((Int) startObj).value();
            int end = ((Int) endObj).value();

            if (expr.isExclusive()) {
                if (start < end) end -= 1;
                else end += 1;
            }

            return new Range(start, end);
        }

        return Undefined.VALUE;
        //throw new ComputeException("Start and end of range must be integers", expr.getPosition());
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
                        start += vector.size();
                    }
                } else {
                    return Undefined.VALUE;
                    //throw new ComputeException("Slice indices must be integers", startExpr.getPosition());
                }
            }

            if (endExpr == null) {
                end = vector.size();
            } else {
                Obj _obj = endExpr.accept(this, scope);
                if (_obj instanceof Int) {
                    end = ((Int) _obj).value();
                    if (end < 0) {
                        end += vector.size();
                    }
                } else {
                    return Undefined.VALUE;
                    //throw new ComputeException("Slice indices must be integers", endExpr.getPosition());
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
                    //throw new ComputeException("Slice indices must be integers", stepExpr.getPosition());
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
        //throw new ComputeException("Slice not applicable to " + obj, expr.getPosition());
    }

    public Obj visit(AssignmentExpr expr, Scope scope) {
        if (expr.getFrom() != null) {
            expr.getFrom().accept(this, scope).setAttr(expr.getName(), expr.getExpr().accept(this, scope));
            return Undefined.VALUE;
        }

        if (expr.isDeclare()) {
            scope.declare(expr.getName(), expr.getExpr().accept(this, scope));
        } else {
            scope.assign(expr.getName(), expr.getExpr().accept(this, scope));
        }
        return Undefined.VALUE;
    }

    public Obj visit(GetOperation expr, Scope scope) {
        return expr.getLeft().accept(this, scope).get(expr.getArgument().accept(this, scope));
    }

    public Obj visit(ReturnExpr expr, Scope scope) {
        Obj obj = expr.getExpr().accept(this, scope);
        throw new ReturnException(expr.getPosition(), obj);
    }

    public Obj visit(ConditionalExpr expr, Scope scope) {
        Obj condition = expr.getCondition().accept(this, scope);
        if (condition instanceof Bool) {
            if (condition == Bool.TRUE) {
                return expr.getIfBranch().accept(this, scope);
            } else if (expr.getElseBranch() != null) {
                return expr.getElseBranch().accept(this, scope);
            }
            return Undefined.VALUE;
        }
        return Undefined.VALUE;
        //throw new ComputeException("Condition of if expression did not return boolean", expr.getCondition().getPosition());
    }
}
