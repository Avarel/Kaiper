package xyz.avarel.kaiper.ast.pattern;

import xyz.avarel.kaiper.lexer.Position;

public class RestPattern extends NamedPattern {
    public RestPattern(Position position, String name) {
        super(position, name);
    }

    @Override
    public <R, C> R accept(PatternVisitor<R, C> visitor, C scope) {
        return visitor.accept(this, scope);
    }
}
