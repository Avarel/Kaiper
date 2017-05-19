package xyz.avarel.aje.parser.lexer;

import xyz.avarel.aje.AJEException;

import java.io.*;

public class AJELexer {
    private char queuedChar;
    private long character;
    private boolean eof;
    private long index;
    private long line;
    private char previous;
    private Reader reader;
    private boolean usePrevious;

    public AJELexer(InputStream inputStream) {
        this(new InputStreamReader(inputStream));
    }

    public AJELexer(String s) {
        this(new StringReader(s));
    }

    public AJELexer(Reader reader) {
        this.reader = reader.markSupported()
                ? reader
                : new BufferedReader(reader);
        this.eof = false;
        this.usePrevious = false;
        this.previous = 0;
        this.index = 0;
        this.character = 0;
        this.line = 1;
        this.queuedChar = (char) -1;
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
                    throw syntaxError("Unrecognized `" + c + "`");
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
        return make(new Position(index, line, character), type);
    }

    private Token make(TokenType type, String value) {
        return make(new Position(index, line, character), type, value);
    }

    private Token make(Position position, TokenType type) {
        return new Token(position, type);
    }

    private Token make(Position position, TokenType type, String value) {
        return new Token(position, type, value);
    }

    public String tokensToString() {
        StringBuilder s = new StringBuilder();

        Token t;
        while ((t = readToken()).getType() != TokenType.EOF) {
            s.append(t).append("  ");
        }

        return s.toString();
    }



    public boolean isQueued() {
        return queuedChar != (char) -1;
    }

    // useful for lexer-phase desugaring
    public void queue(char c) {
        queuedChar = c;
    }

    public char popQueue() {
        char c = queuedChar;
        queuedChar = (char) -1;
        return c;
    }


    /**
     * Back up one character. This provides a sort of lookahead capability,
     * so that you can test for a digit or letter before attempting to parse
     * the readToken number or identifier.
     */
    public void back() {
        if (this.usePrevious || this.index <= 0) {
            throw syntaxError("Stepping back two steps is not supported");
        }

        this.index -= 1;
        this.character -= 1;
        this.usePrevious = true;
        this.eof = false;
    }

    /**
     * @return true if at the end of the file and we didn't step back
     */
    public boolean end() {
        return this.eof && !this.usePrevious;
    }


    /**
     * Determine if the source string still contains characters that readToken()
     * can consume.
     * @return true if not yet at the end of the source.
     */
    public boolean more() {
        this.next();
        if (this.end()) {
            return false;
        }
        this.back();
        return true;
    }


    /**
     * Get the readToken character in the source string.
     *
     * @return The readToken character, or 0 if past the end of the source string.
     */
    public char next() {
        int c;
        if (this.usePrevious) {
            this.usePrevious = false;
            this.index += 1;
            this.character += 1;
            return this.previous;
        } else {
            try {
                c = this.reader.read();
            } catch (IOException exception) {
                throw new AJEException(exception);
            }

            if (c <= 0) { // End of stream
                this.eof = true;
                c = 0;
            }
        }
        this.index += 1;
        if (this.previous == '\r') {
            this.line += 1;
            this.character = c == '\n' ? 0 : 1;
        } else if (c == '\n') {
            this.line += 1;
            this.character = 0;
        } else {
            this.character += 1;
        }
        this.previous = (char) c;
        return this.previous;
    }


    /**
     * Consume the next character, and check that
     * it matches a specified character.
     *
     * @param c The character to match.
     * @return The character.
     */
    public char next(char c) {
        char n = this.next();
        if (n != c) {
            throw this.syntaxError("Expected '" + c + "' and instead saw '" + n + "'");
        }
        return n;
    }


    /**
     * Get the next n characters.
     *
     * @param n The number of characters to take.
     * @return A string of n characters.
     * @throws AJEException
     * Substring bounds error if there are not
     * n characters remaining in the source string.
     */
    public String next(int n) {
        if (n == 0) {
            return "";
        }

        char[] chars = new char[n];
        int pos = 0;

        while (pos < n) {
            chars[pos] = this.next();
            if (this.end()) {
                throw this.syntaxError("Substring bounds error");
            }
            pos += 1;
        }
        return new String(chars);
    }


    /**
     * Get the next char in the string, skipping whitespace.
     * @return A character, or 0 if there are no more characters.
     */
    public char nextClean() {
        while (true) {
            char c = this.next();
            if (c == 0 || c > ' ') {
                return c;
            }
        }
    }

    /**
     * Get the text up but not including the specified character or the
     * end of line, whichever comes first.
     * @param  delimiter A delimiter character.
     * @return A string.
     */
    public String nextTo(char delimiter) {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = this.next();
            if (c == delimiter || c == 0 || c == '\n' || c == '\r') {
                if (c != 0) {
                    this.back();
                }
                return sb.toString().trim();
            }
            sb.append(c);
        }
    }


    /**
     * Get the text up but not including one of the specified delimiter
     * characters or the end of line, whichever comes first.
     * @param delimiters A set of delimiter characters.
     * @return A string, trimmed.
     */
    public String nextTo(String delimiters) {
        char c;
        StringBuilder sb = new StringBuilder();
        while (true) {
            c = this.next();
            if (delimiters.indexOf(c) >= 0 || c == 0 ||
                    c == '\n' || c == '\r') {
                if (c != 0) {
                    this.back();
                }
                return sb.toString().trim();
            }
            sb.append(c);
        }
    }


    /**
     * Skip characters until the readToken character is the requested character.
     * If the requested character is not found, no characters are skipped.
     * @param to A character to skip to.
     * @return The requested character, or zero if the requested character
     * is not found.
     */
    public char skipTo(char to) {
        char c;
        try {
            long startIndex = this.index;
            long startCharacter = this.character;
            long startLine = this.line;
            this.reader.mark(1000000);
            do {
                c = this.next();
                if (c == 0) {
                    this.reader.reset();
                    this.index = startIndex;
                    this.character = startCharacter;
                    this.line = startLine;
                    return c;
                }
            } while (c != to);
        } catch (IOException exception) {
            throw new AJEException(exception);
        }
        this.back();
        return c;
    }


    /**
     * Make an AJEException to signal a syntax error.
     *
     * @param message The error message.
     * @return  A AJEException object, suitable for throwing
     */
    public AJEException syntaxError(String message) {
        return new AJEException(message + this.toString());
    }

    /**
     * Make an AJEException to signal a syntax error.
     *
     * @param message The error message.
     * @param causedBy The throwable that caused the error.
     * @return  A AJEException object, suitable for throwing
     */
    public AJEException syntaxError(String message, Throwable causedBy) {
        return new AJEException(message + this.toString(), causedBy);
    }

    /**
     * Make a printable string of this JSONTokener.
     *
     * @return " at {index} [character {character} line {line}]"
     */
    @Override
    public String toString() {
        return " at " + this.index + " [line " + this.line + " : char " + this.character + "]";
    }
}