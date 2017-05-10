package xyz.avarel.aje.parserRewrite;

public enum TokenType {
    LEFT_PAREN('('),
    RIGHT_PAREN(')'),

    LEFT_BRACKET('['),
    RIGHT_BRACKET(']'),

    COMMA(','),
    DOT('.'),

    EQUALS('='),
    GREATER_THAN('>'),
    LESS_THAN('<'),
    AMPERSAND('&'),
    PIPE('|'),

    PLUS('+'),
    MINUS('-'),
    ASTERISK('*'),
    SLASH('/'),
    BACKSLASH('\\'),
    CARET('^'),
    PERCENT('%'),

    TILDE('~'),
    BANG('!'),
    QUESTION('?'),
    COLON(':'),

    NAME,
    NUMERIC,
    EOF,
    UNKNOWN;

    private final Character punctuator;

    TokenType() {
        this(null);
    }

    TokenType(Character punctuator) {
        this.punctuator = punctuator;
    }

    public Character punctuator() {
        return punctuator;
    }
}
