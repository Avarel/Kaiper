package xyz.avarel.aje.ast;

import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

public class ConditionalExpr implements Expr {
    private final Expr condition;
    private final Expr ifBranch;
    private final Expr elseBranch;

    public ConditionalExpr(Expr condition, Expr ifBranch, Expr elseBranch) {
        this.condition = condition;
        this.ifBranch = ifBranch;
        this.elseBranch = elseBranch;
    }

    public Expr getCondition() {
        return condition;
    }

    public Expr getIfBranch() {
        return ifBranch;
    }

    public Expr getElseBranch() {
        return elseBranch;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) { // return if (sin(0) == 0) sin(50) else false
        builder.append(indent).append(isTail ? "└── " : "├── ").append("if\n");

        condition.ast("condition", builder, indent + (isTail ? "    " : "│   "), false);
        builder.append('\n');

        ifBranch.ast("true", builder, indent + (isTail ? "    " : "│   "), false);

        if (elseBranch != null) {
            builder.append('\n');
            elseBranch.ast("false", builder, indent + (isTail ? "    " : "│   "), true);
        }
    }
}
