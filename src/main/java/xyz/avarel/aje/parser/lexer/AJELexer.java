package xyz.avarel.aje.parser.lexer;

import xyz.avarel.aje.AJEException;

import java.util.Iterator;

public class AJELexer implements Iterator<Token>, Iterable<Token> {
    private final String str;
    private int pos = -1;

    public AJELexer() {
        this("");
    }

    /**
     * Creates a new Lexer to tokenize the given string.
     *
     * @param str String to tokenize.
     */
    public AJELexer(String str) {
        this.str = str;
    }

    @Override
    public boolean hasNext() {
        return pos < str.length();
    }

    private char advance() {
        return ++pos < str.length() ? str.charAt(pos) : (char) -1;
    }

    private char peek() {
        return pos < str.length() - 1 ? str.charAt(pos + 1) : (char) -1;
    }

    private boolean match(char prompt) {
        if (peek() == prompt) {
            advance();
            return true;
        }
        return false;
    }

    public Token next(TokenType type) {
        Token token = next();
        if (token.getType() != type) {
            throw error("Expected token " + type + " but got " + token.getType(), token.getPos());
        }
        return token;
    }

    @Override
    public Token next() {
        char c = advance();

        while (c == ' ') c = advance();

        switch (c) {
            case '(': return make(TokenType.LEFT_PAREN);
            case ')': return make(TokenType.RIGHT_PAREN);

            case '[': return make(TokenType.LEFT_BRACKET);
            case ']': return make(TokenType.RIGHT_BRACKET);

            case '{': return make(TokenType.LEFT_BRACE);
            case '}': return make(TokenType.RIGHT_BRACE);

            case '_': return make(TokenType.UNDERSCORE);

            case '.': return match('.')
                    ? make(TokenType.RANGE_TO)
                    : make(TokenType.DOT);
            case ',': return make(TokenType.COMMA);
            case '!': return match('=')
                    ? make(TokenType.NOT_EQUAL)
                    : make(TokenType.BANG);
            case '?': return make(TokenType.QUESTION);
            case '~': return make(TokenType.TILDE);

            case '+': return make(TokenType.PLUS);
            case '-': return match('>')
                    ? make(TokenType.ARROW)
                    : make(TokenType.MINUS);
            case '*': return make(TokenType.ASTERISK);
            case '/': return match('\\')
                    ? make(TokenType.AND)
                    : make(TokenType.SLASH);
            case '%': return make(TokenType.PERCENT);
            case '^': return make(TokenType.CARET);

            case '\\': return match('/')
                    ? make(TokenType.OR)
                    : make(TokenType.BACKSLASH);

            case ':': return make(TokenType.COLON);

            case '=': return match('=')
                    ? make(TokenType.EQUALS)
                    : make(TokenType.ASSIGN);
            case '>': return match('=')
                    ? make(TokenType.GTE)
                    : make(TokenType.GT);
            case '<': return match('=')
                    ? make(TokenType.LTE)
                    : make(TokenType.LT);
            case '|': return match('|')
                    ? make(TokenType.OR)
                    : match('>')
                    ? make(TokenType.PIPE_FORWARD)
                    : make(TokenType.VERTICAL_BAR);
            case '&': return match('&')
                    ? make(TokenType.AND)
                    : make(TokenType.AMPERSAND);

            case ';': case '\n': case '\r': return make(TokenType.LINE);

            case '\0': return make(TokenType.EOF);

            default:
                if (Character.isDigit(c)) {
                    return nextNumber();
                } else if (Character.isLetter(c)) {
                    return nextName();
                } else {
                    if (pos == str.length()) return make(TokenType.EOF);
                    throw error("Could not lex `" + c + "`");
                }
        }

    }

    private Token nextNumber() {
        int start = pos;
        boolean point = false;

        while (Character.isDigit(peek()) || peek() == '.') {
            if (match('.')) {
                if (peek() == '.') {
                    pos--;
                    break;
                } else if (!Character.isDigit(peek())) {
                    pos--;
                    break;
                } else if (!point) {
                    point = true;
                }
            }
            advance();
        }

        if (Character.isLetter(peek()) && peek() != 'i') {
            throw error("Numbers can not be followed up by letters");
        }

        String value = str.substring(start, pos + 1);

        if (!point) return make(TokenType.INT, value);

        return make(TokenType.DECIMAL, value);
    }

    private Token nextName() {
        int start = pos;

        while (Character.isLetterOrDigit(peek())) {
            advance();
        }

        String value = str.substring(start, pos + 1);

        return nameOrKeyword(start, value);
    }

    private Token nameOrKeyword(int pos, String value) {
        switch(value) {
            case "fun": return make(pos, TokenType.FUNCTION);
            case "true": return make(pos, TokenType.BOOLEAN, "true");
            case "false": return make(pos, TokenType.BOOLEAN, "false");
            case "i": return make(pos, TokenType.IMAGINARY);
            case "and": return make(pos, TokenType.AND);
            case "or": return make(pos, TokenType.OR);
            default: return make(pos, TokenType.NAME, value);
        }
    }

    private Token make(TokenType type) {
        return make(pos, type);
    }

    private Token make(TokenType type, String value) {
        return make(pos, type, value);
    }

    private Token make(int pos, TokenType type) {
        return new Token(pos, type);
    }

    private Token make(int pos, TokenType type, String value) {
        return new Token(pos, type, value);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        for (Token t : this) {
            s.append(t).append("  ");
        }

        return s.toString();
    }


    @Override
    public Iterator<Token> iterator() {
        return this;
    }


    public AJEException error(String message) {
        return error(message, pos);
    }

    public AJEException error(String message, int position) {
        return new AJEException(message + ", position " + position + ".");
    }
}