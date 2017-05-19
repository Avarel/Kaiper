package xyz.avarel.aje.ast.invocation;

import xyz.avarel.aje.ast.Expr;
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

        return left.compute().identity().invoke(arguments);
    }


    List<Expr> getExprs() {
        return exprs;
    }

    @Override
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append("invoke\n");
        left.ast(builder, prefix + (isTail ? "    " : "│   "), false);
        builder.append('\n');
        for (int i = 0; i < exprs.size() - 1; i++) {
            exprs.get(i).ast(builder, prefix + (isTail ? "    " : "│   "), false);
            builder.append('\n');
        }
        if (exprs.size() > 0) {
            exprs.get(exprs.size() - 1).ast(builder, prefix + (isTail ? "    " : "│   "), true);
        }
    }
}
