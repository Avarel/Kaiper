package xyz.avarel.aje.ast;

import xyz.avarel.aje.parser.lexer.Position;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

public class ConditionalExpr extends Expr {
    private final Expr condition;
    private final Expr ifBranch;
    private final Expr elseBranch;

    public ConditionalExpr(Position position, Expr condition, Expr ifBranch, Expr elseBranch) {
        super(position);
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

        ifBranch.ast("true", builder, indent + (isTail ? "    " : "│   "), elseBranch == null);

        if (elseBranch != null) {
            builder.append('\n');
            elseBranch.ast("false", builder, indent + (isTail ? "    " : "│   "), true);
        }
    }
}
