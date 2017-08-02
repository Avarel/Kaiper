package xyz.avarel.kaiper.ast.pattern;

import xyz.avarel.kaiper.lexer.Position;

// a: is Int
// a: 2
// a: x
// a: (2, meme: 2, dank: 3)
public class TuplePattern extends NamedPattern {
    private final Pattern pattern;

    public TuplePattern(Position position, String name, Pattern pattern) {
        super(position, name);
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public <R, C> R accept(PatternVisitor<R, C> visitor, C scope) {
        return visitor.accept(this, scope);
    }

    @Override
    public String toString() {
        return getName() + ": " + pattern;
    }
}
