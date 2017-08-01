package xyz.avarel.kaiper.ast.pattern;

import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.lexer.Position;

// is Int
public class TypePattern extends Pattern {
    private final Single typeExpr;

    public TypePattern(Position position, Single typeExpr) {
        super(position);
        this.typeExpr = typeExpr;
    }

    public Single getTypeExpr() {
        return typeExpr;
    }

    @Override
    public <R, C> R accept(PatternVisitor<R, C> visitor, C scope) {
        return visitor.accept(this, scope);
    }

    @Override
    public String toString() {
        return "is " + typeExpr;
    }
}
