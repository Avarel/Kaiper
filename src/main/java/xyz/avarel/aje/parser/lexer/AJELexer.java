package xyz.avarel.aje.parser.lexer;

import xyz.avarel.aje.AJEException;

import java.util.Iterator;

public class AJELexer implements Iterator<Token>, Iterable<Token> {
    private final String str;
    private int pos = -1;

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

    @Override
    public Token next() {
        char c = advance();

        while (c == ' ') c = advance();

        switch (c) {
            case '(': return new Token(TokenType.LEFT_PAREN);
            case ')': return new Token(TokenType.RIGHT_PAREN);

            case '[': return new Token(TokenType.LEFT_BRACKET);
            case ']': return new Token(TokenType.RIGHT_BRACKET);

            case '{': return new Token(TokenType.LEFT_BRACE);
            case '}': return new Token(TokenType.RIGHT_BRACE);

            case '.': return match('.')
                    ? new Token(TokenType.RANGE_TO)
                    : new Token(TokenType.DOT);
            case ',': return new Token(TokenType.COMMA);

            case '!': return new Token(TokenType.BANG);
            case '?': return new Token(TokenType.QUESTION);
            case '~': return new Token(TokenType.TILDE);

            case '+': return new Token(TokenType.PLUS);
            case '-': return match('>')
                    ? new Token(TokenType.ARROW)
                    : new Token(TokenType.MINUS);
            case '*': return new Token(TokenType.ASTERISK);
            case '/': return new Token(TokenType.SLASH);
            case '%': return new Token(TokenType.PERCENT);
            case '\\': return new Token(TokenType.BACKSLASH);
            case '^': return new Token(TokenType.CARET);
            case ':': return new Token(TokenType.COLON);

            case '=': return match('=')
                    ? new Token(TokenType.EQUALS)
                    : new Token(TokenType.ASSIGN);
            case '>': return match('=')
                    ? new Token(TokenType.GTE)
                    : new Token(TokenType.GT);
            case '<': return match('=')
                    ? new Token(TokenType.LTE)
                    : new Token(TokenType.LT);

            case '|': return match('|')
                    ? new Token(TokenType.OR)
                    : new Token(TokenType.PIPE);
            case '&': return match('&')
                    ? new Token(TokenType.AND)
                    : new Token(TokenType.AMPERSAND);

            case ';': case '\n': case '\r': return new Token(TokenType.LINE);

            case '\0': return new Token(TokenType.EOF);

            default:
                if (Character.isDigit(c)) {
                    return nextNumber();
                } else if (Character.isLetter(c)) {
                    return nextName();
                } else {
                    if (pos == str.length()) return new Token(TokenType.EOF);
                    throw new AJEException("Can not lex " + c);
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
                    break;
                } else if (!point) {
                    point = true;
                }
            }
            advance();
        }

        String value = str.substring(start, pos + 1);

        if (!point) return new Token(TokenType.INT, value);

        return new Token(TokenType.DECIMAL, value);
    }

    private Token nextName() {
        int start = pos;

        while (Character.isLetterOrDigit(peek())) {
            advance();
        }

        String value = str.substring(start, pos + 1);

        return nameOrKeyword(value);
    }

    private Token nameOrKeyword(String value) {
        switch(value) {
            case "fun": return new Token(TokenType.FUNCTION);
            case "i": return new Token(TokenType.IMAGINARY);
            case "and": return new Token(TokenType.AND);
            case "or": return new Token(TokenType.OR);
            default: return new Token(TokenType.NAME, value);
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        for (Token t : this) {
            s.append(t).append("    ");
        }

        return s.toString();
    }

    @Override
    public Iterator<Token> iterator() {
        return this;
    }
}