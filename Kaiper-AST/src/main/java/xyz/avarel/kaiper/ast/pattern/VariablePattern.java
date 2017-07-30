package xyz.avarel.kaiper.ast.pattern;

import xyz.avarel.kaiper.lexer.Position;

// x
// x is Int
public class VariablePattern extends Pattern {
    private final String name;
    private final Pattern pattern;

    protected VariablePattern(Position position, String name, Pattern pattern) {
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
}
