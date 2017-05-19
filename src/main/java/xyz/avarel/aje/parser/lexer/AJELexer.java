package xyz.avarel.aje.parser.lexer;

import xyz.avarel.aje.AJEException;

public class AJELexer extends NewLexer {
    public AJELexer() {
        this("");
    }

    /**
     * Creates a new Lexer to tokenize the given string.
     *
     * @param str String to tokenize.
     */
    public AJELexer(String str) {
        super(str);
    }

    private char peek() {
        char c = next();
        back();
        return c;
    }

    private boolean match(char prompt) {
        if (next() == prompt) {
            return true;
        }
        back();
        return false;
    }

    public Token readToken() {
        char c = isQueued() ? popQueue() : next();

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
            case '\n': return make(TokenType.LINE);

            case '\0': return make(TokenType.EOF);

            case (char) -1: return make(TokenType.EOF);

            default:
                if (Character.isDigit(c)) {
                    return nextNumber(c);
                }
                else if (Character.isLetter(c)) {
                    return nextName(c);
                } else {
                    if (end()) return make(TokenType.EOF);
                    throw error("Could not lex `" + c + "`");
                }
        }

    }

    private Token nextNumber(char init) {
        boolean point = false;

        char c;

        StringBuilder sb = new StringBuilder();
        sb.append(init);

        while (true) {
            c = next();

            if (Character.isDigit(c)) {
                sb.append(c);
            } else switch (c) {
                case '.':
                    if (point) {
                        back();
                        return make(TokenType.DECIMAL, sb.toString());
                    }

                    if (!Character.isDigit(peek())) {
                        queue(c);
                        return make(TokenType.INT, sb.toString());
                    }

                    sb.append(c);
                    point = true;
                    break;
                case '_':
                    break;
                default:
                    back();
                    if (Character.isAlphabetic(peek()) || peek() == '(') {
                        queue('*');
                    }
                    if (point) {
                        return make(TokenType.DECIMAL, sb.toString());
                    } else {
                        return make(TokenType.INT, sb.toString());
                    }
            }
        }
    }

    private Token nextName(char init) {
        StringBuilder sb = new StringBuilder();
        sb.append(init);

        char c;
        while (true) {
            c = next();

            if (Character.isLetterOrDigit(c)) {
                sb.append(c);
            } else {
                break;
            }
        }

        back();

        return nameOrKeyword(sb.toString());
    }

    private Token nameOrKeyword(String value) {
        switch(value) {
            case "fun": return make(TokenType.FUNCTION);
            case "true": return make(TokenType.BOOLEAN, "true");
            case "false": return make(TokenType.BOOLEAN, "false");
            case "i": return make(TokenType.IMAGINARY);
            case "and": return make(TokenType.AND);
            case "or": return make(TokenType.OR);
            default: return make(TokenType.NAME, value);
        }
    }

    private Token make(TokenType type) {
        return make(0, type);
    }

    private Token make(TokenType type, String value) {
        return make(0, type, value);
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
        return error(message, 0);
    }

    public AJEException error(String message, int position) {
        return new AJEException(message + ", position " + position + ".");
    }
}