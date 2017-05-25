package xyz.avarel.aje.runtime.functions;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Type;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.scope.Scope;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Every operation results in the same
 * instance, NOTHING.
 */
public class CompiledFunction extends AJEFunction {
    private final List<Parameter> parameters;
    private final Expr expr;
    private final Scope scope;

    public CompiledFunction(List<Parameter> parameters, Expr expr, Scope scope) {
        this.parameters = parameters;
        this.expr = expr;
        this.scope = scope;
    }

    @Override
    public int getArity() {
        return parameters.size();
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "func(" + parameters.stream().map(Object::toString).collect(Collectors.joining(",")) + ")";
    }

    @Override
    public Obj invoke(List<Obj> args) {
        for (int i = 0; i < getArity(); i++) {
            Parameter parameter = parameters.get(i);
            Obj obj = parameter.getType().accept(new ExprVisitor(), scope);
            if (obj instanceof Type) {
                Type type = (Type) obj;
                if (i < args.size()) {
                    if (args.get(i).getType().is(type)) {
                        scope.declare(parameter.getName(), args.get(i));
                    } else {
                        return Undefined.VALUE;
                    }
                } else if (parameter.hasDefault()) {
                    scope.declare(parameter.getName(), parameter.getDefault().accept(new ExprVisitor(), scope));
                } else {
                    return Undefined.VALUE;
                }
            } else {
                return Undefined.VALUE;
            }
        }

        return expr.accept(new ExprVisitor(), scope);
    }
}
