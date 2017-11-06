/*
 *  Copyright 2017 An Tran and Adrian Todt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package xyz.avarel.kaiper.lexer;

import xyz.avarel.kaiper.exceptions.SyntaxException;

import java.io.*;

import static xyz.avarel.kaiper.lexer.TokenType.LEFT_BRACE;

public class KaiperLexer extends Lexer<Token> {
    public KaiperLexer(InputStream inputStream) {
        this(new InputStreamReader(inputStream));
    }

    public KaiperLexer(String s) {
        this(new StringReader(s));
    }

    public KaiperLexer(Reader reader) {
        this(reader, 2);
    }

    public KaiperLexer(Reader reader, int historyBuffer) {
        super(reader, historyBuffer);

        this.current = 0;
        this.index = -1;
        this.lineIndex = 0;
        this.line = 1;

        do {
            readTokens();
        } while (hasNext());

        if (lastToken().getType() != TokenType.EOF) {
            tokens.add(new Token(getPosition(), TokenType.EOF));
        }

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void readTokens() {
        if (!hasNext()) {
            tokens.add(make(TokenType.EOF));
            return;
        }

        char c = advance();

        while (Character.isSpaceChar(c) || c == '\t') c = advance();

        switch (c) {
            case '(': {
                push(make(TokenType.LEFT_PAREN));
                return;
            }
            case ')': {
                if (lastToken().getType() == TokenType.LINE) {
                    tokens.remove(tokens.size() - 1);
                }

                push(make(TokenType.RIGHT_PAREN));
                return;
            }

            case '[': {
                push(make(TokenType.LEFT_BRACKET));
                return;
            }
            case ']': {
                if (lastToken().getType() == TokenType.LINE) {
                    tokens.remove(tokens.size() - 1);
                }

                push(make(TokenType.RIGHT_BRACKET));
                return;
            }

            case '{': {
                push(make(LEFT_BRACE));
                return;
            }
            case '}': {
                if (lastToken().getType() == TokenType.LINE) {
                    tokens.remove(tokens.size() - 1);
                }

                push(make(TokenType.RIGHT_BRACE));
                return;
            }

            case '_': {
                StringBuilder builder = new StringBuilder().append(c);
                while (match('_')) {
                    builder.append('_');
                }

                if (Character.isLetterOrDigit(peek())) {
                    readName(builder.toString());
                    return;
                }

                push(make(TokenType.UNDERSCORE, builder.toString()));
                return;
            }

            case '.': {
                Token token = match('.')
                        ? match('.')
                        ? make(TokenType.REST)
                        : make(TokenType.RANGE_TO)
                        : make(TokenType.DOT);
                push(token);
                return;
            }
            case ',': {
                if (lastToken().getType() == TokenType.LINE) {
                    tokens.remove(tokens.size() - 1);
                }

                push(make(TokenType.COMMA));
                return;
            }
            case '!': {
                Token token = match('=')
                        ? make(TokenType.NOT_EQUAL)
                        : make(TokenType.BANG);
                push(token);
                return;
            }
            case '?': {
                Token token = match('=')
                        ? make(TokenType.OPTIONAL_ASSIGN)
                        : match(':')
                        ? make(TokenType.ELVIS)
                        : make(TokenType.QUESTION);
                push(token);
                return;
            }
            case '~': {
                push(make(TokenType.TILDE));
                return;
            }

            case '+': {
                push(make(TokenType.PLUS));
                return;
            }
            case '-': {
                Token token = match('>')
                        ? make(TokenType.ARROW)
                        : make(TokenType.MINUS);
                push(token);
                return;
            }
            case '*': {
                push(make(TokenType.ASTERISK));
                return;
            }
            case '/': {
                Token token;
                if (match('\\')) {
                    token = make(TokenType.AND);
                } else if (match('/')) {
                    readComment();
                    return;
                } else if (match('*')) {
                    nextBlockComment();
                    return;
                } else {
                    token = make(TokenType.SLASH);
                }
                push(token);
                return;
            }
            case '%': {
                push(make(TokenType.PERCENT));
                return;
            }
            case '^': {
                push(make(TokenType.CARET));
                return;
            }

            case '\\': {
                Token token = match('/')
                        ? make(TokenType.OR)
                        : make(TokenType.BACKSLASH);
                push(token);
                return;
            }

            case ':': {
                Token token = match(':')
                        ? make(TokenType.REF)
                        : make(TokenType.COLON);
                push(token);
                return;
            }

            case '=': {
                Token token = match('=') // ==
                        ? make(TokenType.EQUALS)
                        : make(TokenType.ASSIGN); // >
                push(token);
                return;
            }

            case '>': {
                Token token = match('=') // >=
                        ? make(TokenType.GTE)
                        : match('>') // >>
                        ? make(TokenType.SHIFT_RIGHT)
                        : make(TokenType.GT); // >
                push(token);
                return;
            }

            case '<': {
                Token token = match('=') // <=
                        ? make(TokenType.LTE)
                        : match('|') // <|
                        ? make(TokenType.PIPE_BACKWARD)
                        : match('<') // <<
                        ? make(TokenType.SHIFT_LEFT)
                        : make(TokenType.LT); // <
                push(token);
                return;
            }

            case '|': {
                Token token; // |
                if (match('|')) {
                    token = make(TokenType.OR);
                } else if (match('>')) {
                    if (lastToken().getType() == TokenType.LINE) {
                        tokens.remove(tokens.size() - 1);
                    }
                    token = make(TokenType.PIPE_FORWARD);
                } else {
                    token = make(TokenType.VERTICAL_BAR);
                }
                push(token);
                return;
            }

            case '&': {
                Token token = match('&') // &&
                        ? make(TokenType.AND)
                        : make(TokenType.AMPERSAND); // &
                push(token);
                return;
            }

            case '"': {
                readString('"', true);
                return;
            }
            case '\'': {
                readString('\'', false);
                return;
            }

            case ';': {
                match('\r');
                match('\n');

                Token token = make(TokenType.LINE);

                if (!tokens.isEmpty() && lastToken().getType() != TokenType.LINE) {
                    push(token);
                }

                return;
            }
            case '\r':
            case '\n': {
                if (!tokens.isEmpty()) {
                    switch (lastToken().getType()) {
                        case LEFT_BRACE:
                        case LEFT_BRACKET:
                        case LEFT_PAREN:
                        case ARROW:
                        case COMMA:
                        case LINE:
                            break;
                        default:
                            push(make(TokenType.LINE));
                    }
                }
                return;
            }

            case '\0': {
                push(make(TokenType.EOF));
                return;
            }

            case (char) -1: {
                push(make(TokenType.EOF));
                return;
            }

            default: {
                if (Character.isDigit(c)) {
                    readNumber(c);
                    return;
                } else if (Character.isLetter(c) || c == '$') {
                    readName(c);
                    return;
                }
                throw new SyntaxException("Unrecognized `" + c + "`", getPosition());
            }
        }
    }

    private void readComment() {
        while (hasNext() && peek() != '\n') {
            advance();
        }
    }

    private void nextBlockComment() {
        while (hasNext()) {
            if (peek() == '*') {
                advance();
                if (peek() == '/') {
                    advance();
                    break;
                }
                back();
            }
            advance();
        }
    }

    private void readNumber(char init) {
        boolean point = false;

        char c;

        StringBuilder sb = new StringBuilder();
        sb.append(init);

        while (true) {
            c = advance();

            if (Character.isDigit(c)) {
                sb.append(c);
            } else switch (c) {
                case 'i': {
                    back();
                    push(make(TokenType.NUMBER, sb.toString()));
                    return;
                }
                case '.': {
                    if (point) {
                        back();
                        Token token = make(TokenType.NUMBER, sb.toString());
                        push(token);
                        return;
                    }

                    if (!Character.isDigit(peek())) {
                        back();
                        push(make(TokenType.INT, sb.toString()));
                        return;
                    }

                    sb.append(c);
                    point = true;
                    break;
                }
                case '_':
                    break;
                default: {
                    back();
                    if (point) {
                        push(make(TokenType.NUMBER, sb.toString()));
                        return;
                    } else {
                        push(make(TokenType.INT, sb.toString()));
                        return;
                    }
                }
            }
        }
    }

    private void readName(char init) {
        readName(String.valueOf(init));
    }

    private void readAtom() {
        StringBuilder sb = new StringBuilder();

        char c;
        while (true) {
            c = advance();

            if (Character.isLetterOrDigit(c) || c == '_') {
                sb.append(c);
            } else {
                break;
            }
        }

        back();

        Token token = make(TokenType.ATOM, sb.toString());
        push(token);
    }

    private void readName(String init) {
        StringBuilder sb = new StringBuilder(init);

        char c;
        while (true) {
            c = advance();

            if (Character.isLetterOrDigit(c) || c == '_') {
                sb.append(c);
            } else {
                break;
            }
        }

        back();

        Token token;
        String value = sb.toString();
        switch (value) {
            case "type":
                token = make(TokenType.TYPE, "type");
                break;
            case "module":
                token = make(TokenType.MODULE, "module");
                break;

            case "match":
                token = make(TokenType.MATCH, "match");
                break;
            case "if":
                token = make(TokenType.IF, "if");
                break;
            case "else":
                token = make(TokenType.ELSE, "else");
                break;
            case "return":
                token = make(TokenType.RETURN, "return");
                break;
            case "let":
                token = make(TokenType.LET, "let");
                break;
            case "for":
                token = make(TokenType.FOR, "for");
                break;
            case "null":
                token = make(TokenType.NULL, "null");
                break;

            case "def":
                token = make(TokenType.FUNCTION, "def");
                break;

            case "true":
                token = make(TokenType.BOOLEAN, "true");
                break;
            case "false":
                token = make(TokenType.BOOLEAN, "false");
                break;
            default:
                token = make(TokenType.IDENTIFIER, value);
        }
        push(token);
    }

    public void readString(char quote, boolean template) {
        char c;
        StringBuilder sb = new StringBuilder();
        while (true) {
            c = this.advance();
            switch (c) {
                case 0:
                case '\r':
                case '\n':
                    throw new SyntaxException("Unterminated string.");
                case '\\':
                    c = this.advance();
                    switch (c) {
                        case 'b':
                            sb.append('\b');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case 'n':
                            sb.append('\n');
                            break;
                        case 'f':
                            sb.append('\f');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case 'u':
                            try {
                                sb.append((char) Integer.parseInt(this.advance(4), 16));
                            } catch (NumberFormatException e) {
                                throw new SyntaxException("Illegal escape.", e);
                            }
                            break;
                        case '"':
                        case '\'':
                        case '\\':
                        case '/':
                            sb.append(c);
                            break;
                        default:
                            throw new SyntaxException("Illegal escape.");
                    }
                    break;
                case '$': // String templates
                    if (template) {
                        if (Character.isLetter(peek())) {
                            push(make(TokenType.STRING, sb.toString()));
                            sb.setLength(0);
                            push(make(TokenType.PLUS));
                            readName(advance());

                            if (peek() != quote) {
                                push(make(TokenType.PLUS));
                            } else {
                                advance();
                                return;
                            }
                            break;
                        } else if (peek() == '{') {
                            push(make(TokenType.STRING, sb.toString()));
                            sb.setLength(0);
                            push(make(TokenType.PLUS));

                            advance();
                            while (peek() != '}') {
                                if (!hasNext()) {
                                    throw new SyntaxException("Unterminated template.");
                                }
                                sb.append(advance());
                            }
                            advance();

                            tokens.add(make(TokenType.LEFT_PAREN));
                            tokens.addAll(new KaiperLexer(sb.toString()).tokens);
                            if (lastToken().getType() != TokenType.EOF) {
                                throw new SyntaxException("Internal error");
                            }
                            tokens.remove(tokens.size() - 1);
                            tokens.add(make(TokenType.RIGHT_PAREN));
                            sb.setLength(0);

                            if (peek() != quote) {
                                push(make(TokenType.PLUS));
                            } else {
                                advance();
                                return;
                            }

                            break;
                        }
                        sb.append(c);
                        break;
                    }
                default:
                    if (c == quote) {
                        Token token = make(TokenType.STRING, sb.toString());
                        push(token);
                        return;
                    }
                    sb.append(c);
            }
        }
    }

    private Token make(TokenType type) {
        return make(new Position(index, line, lineIndex), type);
    }

    private Token make(TokenType type, String value) {
        return make(new Position(index - value.length(), line, lineIndex - value.length()), type, value);
    }

    private Token make(Position position, TokenType type) {
        return new Token(position, type);
    }

    private Token make(Position position, TokenType type, String value) {
        return new Token(position, type, value);
    }
}