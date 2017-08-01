package xyz.avarel.kaiper.ast.pattern;

import com.sun.istack.internal.NotNull;
import xyz.avarel.kaiper.lexer.Position;

// a: is Int
// a: 2
// a: x
public class TuplePattern extends Pattern {
    private final String name;
    private final Pattern pattern;

    public TuplePattern(Position position, String name, @NotNull Pattern pattern) {
        super(position);
        this.name = name;
        this.pattern = pattern;
    }

    public String getName() {
        return name;
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
        return name + ": " + pattern;
    }
}
