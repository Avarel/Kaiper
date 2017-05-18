package xyz.avarel.aje.parser.ast.operations;

import xyz.avarel.aje.parser.ast.Expr;
import xyz.avarel.aje.runtime.Any;

import java.util.function.BinaryOperator;

public class BinaryOperation implements Expr {
    private final Expr left;
    private final Expr right;
    private final BinaryOperator<Any> operator;

    public BinaryOperation(Expr left, Expr right, BinaryOperator<Any> operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public Any compute() {
        return operator.apply(left.compute().identity(), right.compute().identity());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        ast(builder, "", true);
        return builder.toString();
    }

    @Override
    public void ast(StringBuilder builder, String prefix, boolean isTail) {
        builder.append(prefix).append(isTail ? "└── " : "├── ").append("binary\n");
        left.ast(builder, prefix + (isTail ? "    " : "│   "), false);
        builder.append('\n');
        right.ast(builder, prefix + (isTail ? "    " : "│   "), true);

//        for (int i = 0; i < children.size() - 1; i++) {
//            children.get(i).ast(builder, prefix + (isTail ? "    " : "│   "), false);
//        }
//        if (children.size() > 0) {
//            children.get(children.size() - 1).ast(builder, prefix + (isTail ? "    " : "│   "), true);
//        }
    }
}
