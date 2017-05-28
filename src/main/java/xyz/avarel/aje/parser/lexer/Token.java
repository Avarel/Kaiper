package xyz.avarel.aje.parser.lexer;

public final class Token {
    private final Position position;
    private final TokenType type;
    private final String str;

    public Token(Position position, TokenType type) {
        this(position, type, null);
    }

    public Token(Position position, TokenType type, String str) {
        this.position = position;
        this.type = type;
        this.str = str;
    }

    public Position getPosition() {
        return position;
    }

    public TokenType getType() {
        return type;
    }

    public String getText() {
        return str;
    }

    @Override
    public String toString() {
        return type.toString();
    }
}