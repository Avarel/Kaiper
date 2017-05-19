package xyz.avarel.aje.ast.operations;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.runtime.Any;

import java.util.function.UnaryOperator;

public class UnaryOperation implements Expr {
    private final Expr left;
    private final UnaryOperator<Any> operator;

    public UnaryOperation(Expr left, UnaryOperator<Any> operator) {
        this.left = left;
        this.operator = operator;
    }

    @Override
    public Any compute() {
        return operator.apply(left.compute().identity());
    }

    @Override
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append("unary\n");
        left.ast(builder, prefix + (isTail ? "    " : "│   "), true);
    }
}
