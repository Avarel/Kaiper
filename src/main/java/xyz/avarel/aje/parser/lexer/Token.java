package xyz.avarel.aje.parser.lexer;

public final class Token {
    private final TokenType type;
    private final String str;

    public Token(TokenType type) {
        this(type, type.toString());
    }

    public Token(TokenType type, String str) {
        this.type = type;
        this.str = str;
    }

    public TokenType getType() {
        return type;
    }

    public String getText() {
        return str;
    }

    @Override
    public String toString() {
        return str;
    }
}