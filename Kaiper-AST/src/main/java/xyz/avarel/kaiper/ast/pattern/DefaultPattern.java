package xyz.avarel.kaiper.ast.pattern;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.lexer.Position;

// (delegate) = (defaultExpr)
public class DefaultPattern extends Pattern {
    private final Pattern pattern;
    private final Expr defaultExpr;

    protected DefaultPattern(Position position, Pattern delegate, Expr defaultExpr) {
        super(position);
        this.pattern = delegate;
        this.defaultExpr = defaultExpr;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Expr getDefaultExpr() {
        return defaultExpr;
    }

    @Override
    public <R, C> R accept(PatternVisitor<R, C> visitor, C scope) {
        return visitor.accept(this, scope);
    }
}
