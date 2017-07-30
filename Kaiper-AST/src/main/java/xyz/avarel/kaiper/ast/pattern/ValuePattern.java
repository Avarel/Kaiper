package xyz.avarel.kaiper.ast.pattern;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.lexer.Position;

// literally literals
public class ValuePattern extends Pattern {
    private final Expr value;

    protected ValuePattern(Position position, Expr value) {
        super(position);
        this.value = value;
    }

    public Expr getValue() {
        return value;
    }

    @Override
    public <R, C> R accept(PatternVisitor<R, C> visitor, C scope) {
        return visitor.accept(this, scope);
    }
}
