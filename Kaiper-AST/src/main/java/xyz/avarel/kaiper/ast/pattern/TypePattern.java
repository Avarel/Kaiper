package xyz.avarel.kaiper.ast.pattern;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.lexer.Position;

// is Int
public class TypePattern extends Pattern {
    private final Expr typeExpr;

    protected TypePattern(Position position, Expr typeExpr) {
        super(position);
        this.typeExpr = typeExpr;
    }

    public Expr getTypeExpr() {
        return typeExpr;
    }

    @Override
    public <R, C> R accept(PatternVisitor<R, C> visitor, C scope) {
        return visitor.accept(this, scope);
    }
}
