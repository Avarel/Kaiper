package xyz.avarel.aje.parser.lexer;

public enum TokenType {
    LEFT_PAREN,
    RIGHT_PAREN,

    LEFT_BRACKET,
    RIGHT_BRACKET,

    LEFT_BRACE,
    RIGHT_BRACE,

    COMMA,
    DOT,

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

    INT,
    DECIMAL,

    FUNCTION,
    IMAGINARY,

    NAME,
    EOF,
    LINE,
    UNKNOWN
}
