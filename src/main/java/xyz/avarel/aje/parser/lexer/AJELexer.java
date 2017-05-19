package xyz.avarel.aje.parser.lexer;

import xyz.avarel.aje.AJEException;

import java.io.IOException;
import java.io.Reader;

public class AJELexer {
    private final String string;
    private int pos = -1;

    /**
     * Creates a new Lexer to tokenize the given string.
     *
     * @param string String to tokenize.
     */
    public AJELexer(String string) {
        this.string = string;
    }

    /**
     * @param reader Turn the reader into a String.
     */
    public AJELexer(Reader reader) {
        int value;
        StringBuilder builder = new StringBuilder();
        try {
            while ((value = reader.read()) != -1) {
                builder.append((char) value);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.string = builder.toString();
    }

    private char next() {
        return ++pos < string.length() ? string.charAt(pos) : (char) -1;
    }

    private char peek() {
        return pos < string.length() - 1 ? string.charAt(pos + 1) : (char) -1;
    }

    private boolean match(char prompt) {
        if (peek() == prompt) {
            next();
            return true;
        }
        return false;
    }

    public Token readToken() {
        char c = next();

        while (Character.isSpaceChar(c)) c = next();

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

            case ';': {
                match('\r');
                match('\n');

                return make(TokenType.SEMICOLON);
            }
            case '\r': {
                match('\n');

                return make(TokenType.LINE);
            }
            case '\n': {

                return make(TokenType.LINE);
            }

            case '\0': return make(TokenType.EOF);

            case (char) -1: return make(TokenType.EOF);

            default:
                if (Character.isDigit(c)) {
                    return nextNumber();
                } else if (Character.isLetter(c)) {
                    return nextName();
                } else {
                    if (pos == string.length()) return make(TokenType.EOF);
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
            next();
        }

        if (Character.isLetter(peek()) && peek() != 'i') {
            throw error("Numbers can not be followed up by letters");
        }

        String value = string.substring(start, pos + 1);

        if (!point) return make(TokenType.INT, value);

        return make(TokenType.DECIMAL, value);
    }

    private Token nextName() {
        int start = pos;

        while (Character.isLetterOrDigit(peek())) {
            next();
        }

        String value = string.substring(start, pos + 1);

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

        Token t;
        while ((t = readToken()).getType() != TokenType.EOF) {
            s.append(t).append("  ");
        }

        return s.toString();
    }


    public AJEException error(String message) {
        return error(message, pos);
    }

    public AJEException error(String message, int position) {
        return new AJEException(message + ", position " + position + ".");
    }
}