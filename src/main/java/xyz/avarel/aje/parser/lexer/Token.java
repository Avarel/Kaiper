package xyz.avarel.aje.parser.lexer;

public final class Token {
    private final int pos;
    private final TokenType type;
    private final String str;

    public Token(int pos, TokenType type) {
        this(pos, type, type.toString());
    }

    public Token(int pos, TokenType type, String str) {
        this.pos = pos;
        this.type = type;
        this.str = str;
    }

    public int getPos() {
        return pos;
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