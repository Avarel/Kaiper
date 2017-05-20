package xyz.avarel.aje.runtime.functions;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.runtime.Any;
import xyz.avarel.aje.runtime.Undefined;
import xyz.avarel.aje.runtime.pool.ObjectPool;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Every operation results in the same
 * instance, NOTHING.
 */
public class CompiledFunction extends AJEFunction {
    private final List<String> parameters;
    private final ObjectPool pool;
    private final Expr expr;

    public CompiledFunction(List<String> parameters, Expr expr, ObjectPool pool) {
        this.parameters = parameters;
        this.expr = expr;
        this.pool = pool;
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
    public Any invoke(List<Any> args) {
        if (args.size() != getArity()) {
            return Undefined.VALUE;
        }

        for (int i = 0; i < parameters.size(); i++) {
            //pool.resetState();
            pool.put(parameters.get(i), args.get(i));
        }

        return expr.compute().identity();
    }
}
