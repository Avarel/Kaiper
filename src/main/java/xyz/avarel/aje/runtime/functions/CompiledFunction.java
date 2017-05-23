package xyz.avarel.aje.runtime.functions;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.pool.Scope;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Every operation results in the same
 * instance, NOTHING.
 */
public class CompiledFunction extends AJEFunction {
    private final List<String> parameters;
    private final Expr expr;
    private final Scope scope;

    public CompiledFunction(List<String> parameters, Expr expr, Scope scope) {
        this.parameters = parameters;
        this.expr = expr;
        this.scope = scope;
    }

    @Override
    public int getArity() {
        return parameters.size();
    }

    @Override
    public String toString() {
        return "fun(" + parameters.stream().collect(Collectors.joining(",")) + ")";
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public Obj invoke(List<Obj> args) {
        if (args.size() != getArity()) {
            return Undefined.VALUE;
        }

        for (int i = 0; i < parameters.size(); i++) {
            scope.assign(parameters.get(i), args.get(i));
        }

        return expr.accept(new ExprVisitor(), scope);
    }
}
