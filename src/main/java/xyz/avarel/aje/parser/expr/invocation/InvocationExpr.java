package xyz.avarel.aje.parser.expr.invocation;

import xyz.avarel.aje.parser.expr.Expr;
import xyz.avarel.aje.runtime.Any;

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
            arguments.add(expr.compute().identity());
        }

        return left.compute().invoke(arguments);
    }


    List<Expr> getExprs() {
        return exprs;
    }

    @Override
    public void ast(StringBuilder builder, String indent) {
        builder.append(indent).append("invoke\n");
        left.ast(builder, indent + "│ ");
        builder.append('\n');
        for (Expr expr : exprs) {
            expr.ast(builder, indent + "│ ");
        }
    }
}
