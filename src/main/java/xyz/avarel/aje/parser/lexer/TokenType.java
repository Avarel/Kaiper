package xyz.avarel.aje.parser.lexer;

public enum TokenType {
    // PAIRS
    LEFT_PAREN,
    RIGHT_PAREN,

    LEFT_BRACKET,
    RIGHT_BRACKET,

    LEFT_BRACE,
    RIGHT_BRACE,

    // ASSIGNMENT
    ASSIGN,

    // TYPES
    NAME,
    INT,
    DECIMAL,
    IMAGINARY,
    BOOLEAN,
    FUNCTION,

    // ARITHMETIC
    PLUS,
    MINUS,
    ASTERISK,
    SLASH,
    BACKSLASH,
    CARET,
    PERCENT,

    // RELATIONAL
    EQUALS,
    NOT_EQUAL,
    GT,
    GTE,
    LT,
    LTE,
    OR,
    AND,

    // BOOLEAN
    AMPERSAND,
    VERTICAL_BAR,

    // MISC
    RANGE_TO,
    ARROW,
    PIPE_FORWARD,
    TILDE,
    BANG,
    QUESTION,
    COLON,
    COMMA,
    DOT,

    UNDERSCORE,

    // SCRIPT
    VAR,
    RETURN,
    IF,
    ELSE,

    SKIP,
    SEMICOLON,
    LINE,
    EOF,
}
