package xyz.avarel.kaiper.ast;

import xyz.avarel.kaiper.ast.pattern.PatternCase;
import xyz.avarel.kaiper.lexer.Position;

public class TypeNode extends Single {
    private final String name;
    private final PatternCase patternCase;
    private final Expr expr;

    public TypeNode(Position position, String name, PatternCase patternCase, Expr expr) {
        super(position);
        this.name = name;
        this.patternCase = patternCase;
        this.expr = expr;
    }

    public String getName() {
        return name;
    }

    public PatternCase getPatternCase() {
        return patternCase;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ")
                .append("type").append(name != null ? " " + name : "")
                .append('(').append(patternCase).append(')');

        builder.append('\n');
        expr.ast(builder, indent + (isTail ? "    " : "│   "), true);
    }
}
