package xyz.avarel.kaiper.ast.pattern;

import xyz.avarel.kaiper.ast.Positional;
import xyz.avarel.kaiper.lexer.Position;

public abstract class Pattern extends Positional {
    protected Pattern(Position position) {
        super(position);
    }

    public abstract <R, C> R accept(PatternVisitor<R, C> visitor, C scope);
}
