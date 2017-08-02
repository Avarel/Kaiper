package xyz.avarel.kaiper.ast.pattern;

import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.lexer.Position;

// (delegate) = (defaultExpr)
public class DefaultPattern extends Pattern {
    private final NamedPattern delegate;
    private final Single defaultExpr;

    public DefaultPattern(Position position, NamedPattern delegate, Single defaultExpr) {
        super(position);
        this.delegate = delegate;
        this.defaultExpr = defaultExpr;
    }

    public NamedPattern getDelegate() {
        return delegate;
    }

    public Single getDefaultExpr() {
        return defaultExpr;
    }

    @Override
    public <R, C> R accept(PatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return delegate + " = " + defaultExpr;
    }
}
