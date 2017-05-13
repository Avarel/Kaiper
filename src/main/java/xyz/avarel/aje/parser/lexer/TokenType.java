package xyz.avarel.aje.parser.lexer;

public enum TokenType {
    LEFT_PAREN,
    RIGHT_PAREN,

    LEFT_BRACKET,
    RIGHT_BRACKET,

    LEFT_BRACE,
    RIGHT_BRACE,

    ASSIGN,
    EQUALS,
    GT,
    GTE,
    LT,
    LTE,
    OR,
    AND,

    AMPERSAND,
    PIPE,

    PLUS,
    MINUS,
    ASTERISK,
    SLASH,
    BACKSLASH,
    CARET,
    PERCENT,

    RANGE_TO,
    ARROW,

    TILDE,
    BANG,
    QUESTION,
    COLON,
    COMMA,
    DOT,

    INT,
    DECIMAL,
    BOOLEAN,
    IMAGINARY,

    FUNCTION,

    NAME,
    EOF,
    LINE,
    UNKNOWN
}
