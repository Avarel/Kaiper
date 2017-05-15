package xyz.avarel.aje.parser.expr;

import xyz.avarel.aje.runtime.types.Any;

import java.util.ArrayList;
import java.util.List;

public class InvocationExpr implements Expr {
    private final Expr left;
    private final List<Expr> exprs;

    public InvocationExpr(Expr left, List<Expr> exprs) {
        this.left = left;
        this.exprs = exprs;
    }

    @Override
    public Any compute() {
        List<Any> arguments = new ArrayList<>();

        for (Expr expr : exprs) {
            arguments.add(expr.compute());
        }

        return left.compute().invoke(arguments);
    }
}
