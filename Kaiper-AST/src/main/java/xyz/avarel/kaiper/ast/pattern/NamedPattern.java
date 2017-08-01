package xyz.avarel.kaiper.ast.pattern;

import xyz.avarel.kaiper.lexer.Position;

public abstract class NamedPattern extends Pattern {
    private final String name;

    public NamedPattern(Position position, String name) {
        super(position);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
