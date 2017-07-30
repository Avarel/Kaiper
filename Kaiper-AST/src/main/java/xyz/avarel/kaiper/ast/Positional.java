package xyz.avarel.kaiper.ast;

import xyz.avarel.kaiper.lexer.Position;

public abstract class Positional {
    private final Position position;

    protected Positional(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}
