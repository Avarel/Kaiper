package xyz.avarel.kaiper.ast.pattern;

import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.lexer.Position;

// literally literals
public class ValuePattern extends Pattern {
    private final Single value;

    public ValuePattern(Position position, Single value) {
        super(position);
        this.value = value;
    }

    public Single getValue() {
        return value;
    }

    @Override
    public <R, C> R accept(PatternVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
