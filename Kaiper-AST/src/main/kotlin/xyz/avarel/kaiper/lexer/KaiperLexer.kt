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

package xyz.avarel.kaiper.lexer

import xyz.avarel.kaiper.exceptions.SyntaxException
import xyz.avarel.kaiper.lexer.TokenType.LEFT_BRACE
import java.io.*

class KaiperLexer @JvmOverloads constructor(reader: Reader, historyBuffer: Int = 2) : Lexer<Token>(reader, historyBuffer) {
    constructor(inputStream: InputStream) : this(InputStreamReader(inputStream))

    constructor(s: String) : this(StringReader(s))

    init {
        this.current = 0.toChar()
        this.index = -1
        this.lineIndex = 0
        this.line = 1

        do {
            readTokens()
        } while (hasNext())

        if (lastToken().type != TokenType.EOF) {
            tokens.add(Token(position, TokenType.EOF))
        }

        try {
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun readTokens() {
        if (!hasNext()) {
            tokens.add(make(TokenType.EOF))
            return
        }

        var c = advance()

        while (Character.isSpaceChar(c) || c == '\t') c = advance()

        when (c) {
            '(' -> {
                push(make(TokenType.LEFT_PAREN))
                return
            }
            ')' -> {
                if (lastToken().type == TokenType.LINE) {
                    tokens.removeAt(tokens.size - 1)
                }

                push(make(TokenType.RIGHT_PAREN))
                return
            }

            '[' -> {
                push(make(TokenType.LEFT_BRACKET))
                return
            }
            ']' -> {
                if (lastToken().type == TokenType.LINE) {
                    tokens.removeAt(tokens.size - 1)
                }

                push(make(TokenType.RIGHT_BRACKET))
                return
            }

            '{' -> {
                push(make(LEFT_BRACE))
                return
            }
            '}' -> {
                if (lastToken().type == TokenType.LINE) {
                    tokens.removeAt(tokens.size - 1)
                }

                push(make(TokenType.RIGHT_BRACE))
                return
            }

            '_' -> {
                val builder = StringBuilder().append(c)
                while (match('_')) {
                    builder.append('_')
                }

                if (Character.isLetterOrDigit(peek())) {
                    readName(builder.toString())
                    return
                }

                push(make(TokenType.UNDERSCORE, builder.toString()))
                return
            }

            '.' -> {
                val token = if (match('.'))
                    if (match('.'))
                        make(TokenType.REST)
                    else
                        make(TokenType.RANGE_TO)
                else
                    make(TokenType.DOT)
                push(token)
                return
            }
            ',' -> {
                if (lastToken().type == TokenType.LINE) {
                    tokens.removeAt(tokens.size - 1)
                }

                push(make(TokenType.COMMA))
                return
            }
            '!' -> {
                val token = if (match('='))
                    make(TokenType.NOT_EQUAL)
                else
                    make(TokenType.BANG)
                push(token)
                return
            }
            '?' -> {
                val token = if (match('='))
                    make(TokenType.OPTIONAL_ASSIGN)
                else if (match(':'))
                    make(TokenType.ELVIS)
                else
                    make(TokenType.QUESTION)
                push(token)
                return
            }
            '~' -> {
                push(make(TokenType.TILDE))
                return
            }

            '+' -> {
                push(make(TokenType.PLUS))
                return
            }
            '-' -> {
                val token = if (match('>'))
                    make(TokenType.ARROW)
                else
                    make(TokenType.MINUS)
                push(token)
                return
            }
            '*' -> {
                push(make(TokenType.ASTERISK))
                return
            }
            '/' -> {
                val token: Token
                if (match('\\')) {
                    token = make(TokenType.AND)
                } else if (match('/')) {
                    readComment()
                    return
                } else if (match('*')) {
                    nextBlockComment()
                    return
                } else {
                    token = make(TokenType.SLASH)
                }
                push(token)
                return
            }
            '%' -> {
                push(make(TokenType.PERCENT))
                return
            }
            '^' -> {
                push(make(TokenType.CARET))
                return
            }

            '\\' -> {
                val token = if (match('/'))
                    make(TokenType.OR)
                else
                    make(TokenType.BACKSLASH)
                push(token)
                return
            }

            ':' -> {
                val token = if (match(':'))
                    make(TokenType.REF)
                else
                    make(TokenType.COLON)
                push(token)
                return
            }

            '=' -> {
                val token = if (match('=') // ==
                                    )
                    make(TokenType.EQUALS)
                else
                    make(TokenType.ASSIGN) // >
                push(token)
                return
            }

            '>' -> {
                val token = if (match('=') // >=
                                    )
                    make(TokenType.GTE)
                else if (match('>') // >>
                             )
                    make(TokenType.SHIFT_RIGHT)
                else
                    make(TokenType.GT) // >
                push(token)
                return
            }

            '<' -> {
                val token = if (match('=') // <=
                                    )
                    make(TokenType.LTE)
                else if (match('|') // <|
                             )
                    make(TokenType.PIPE_BACKWARD)
                else if (match('<') // <<
                             )
                    make(TokenType.SHIFT_LEFT)
                else
                    make(TokenType.LT) // <
                push(token)
                return
            }

            '|' -> {
                val token: Token // |
                if (match('|')) {
                    token = make(TokenType.OR)
                } else if (match('>')) {
                    if (lastToken().type == TokenType.LINE) {
                        tokens.removeAt(tokens.size - 1)
                    }
                    token = make(TokenType.PIPE_FORWARD)
                } else {
                    token = make(TokenType.VERTICAL_BAR)
                }
                push(token)
                return
            }

            '&' -> {
                val token = if (match('&') // &&
                                    )
                    make(TokenType.AND)
                else
                    make(TokenType.AMPERSAND) // &
                push(token)
                return
            }

            '"' -> {
                readString('"', true)
                return
            }
            '\'' -> {
                readString('\'', false)
                return
            }

            ';' -> {
                match('\r')
                match('\n')

                val token = make(TokenType.LINE)

                if (!tokens.isEmpty() && lastToken().type != TokenType.LINE) {
                    push(token)
                }

                return
            }
            '\r', '\n' -> {
                if (!tokens.isEmpty()) {
                    when (lastToken().type) {
                        LEFT_BRACE, TokenType.LEFT_BRACKET, TokenType.LEFT_PAREN, TokenType.ARROW, TokenType.COMMA, TokenType.LINE -> {
                        }
                        else -> push(make(TokenType.LINE))
                    }
                }
                return
            }

            '\u0000' -> {
                push(make(TokenType.EOF))
                return
            }

            (-1).toChar() -> {
                push(make(TokenType.EOF))
                return
            }

            else -> {
                if (Character.isDigit(c)) {
                    readNumber(c)
                    return
                } else if (Character.isLetter(c) || c == '$') {
                    readName(c)
                    return
                }
                throw SyntaxException("Unrecognized `$c`", position)
            }
        }
    }

    private fun readComment() {
        while (hasNext() && peek() != '\n') {
            advance()
        }
    }

    private fun nextBlockComment() {
        while (hasNext()) {
            if (peek() == '*') {
                advance()
                if (peek() == '/') {
                    advance()
                    break
                }
                back()
            }
            advance()
        }
    }

    private fun readNumber(init: Char) {
        var point = false

        var c: Char

        val sb = StringBuilder()
        sb.append(init)

        while (true) {
            c = advance()

            if (Character.isDigit(c)) {
                sb.append(c)
            } else
                when (c) {
                    'i' -> {
                        back()
                        push(make(TokenType.NUMBER, sb.toString()))
                        return
                    }
                    '.' -> {
                        if (point) {
                            back()
                            val token = make(TokenType.NUMBER, sb.toString())
                            push(token)
                            return
                        }

                        if (!Character.isDigit(peek())) {
                            back()
                            push(make(TokenType.INT, sb.toString()))
                            return
                        }

                        sb.append(c)
                        point = true
                    }
                    '_' -> {
                    }
                    else -> {
                        back()
                        if (point) {
                            push(make(TokenType.NUMBER, sb.toString()))
                            return
                        } else {
                            push(make(TokenType.INT, sb.toString()))
                            return
                        }
                    }
                }
        }
    }

    private fun readName(init: Char) {
        readName(init.toString())
    }

    private fun readAtom() {
        val sb = StringBuilder()

        var c: Char
        while (true) {
            c = advance()

            if (Character.isLetterOrDigit(c) || c == '_') {
                sb.append(c)
            } else {
                break
            }
        }

        back()

        val token = make(TokenType.ATOM, sb.toString())
        push(token)
    }

    private fun readName(init: String) {
        val sb = StringBuilder(init)

        var c: Char
        while (true) {
            c = advance()

            if (Character.isLetterOrDigit(c) || c == '_') {
                sb.append(c)
            } else {
                break
            }
        }

        back()

        val token: Token
        val value = sb.toString()
        when (value) {
            "type" -> token = make(TokenType.TYPE, "type")
            "module" -> token = make(TokenType.MODULE, "module")

            "match" -> token = make(TokenType.MATCH, "match")
            "if" -> token = make(TokenType.IF, "if")
            "else" -> token = make(TokenType.ELSE, "else")
            "return" -> token = make(TokenType.RETURN, "return")
            "let" -> token = make(TokenType.LET, "let")
            "for" -> token = make(TokenType.FOR, "for")
            "null" -> token = make(TokenType.NULL, "null")

            "def" -> token = make(TokenType.FUNCTION, "def")

            "true" -> token = make(TokenType.BOOLEAN, "true")
            "false" -> token = make(TokenType.BOOLEAN, "false")
            else -> token = make(TokenType.IDENTIFIER, value)
        }
        push(token)
    }

    fun readString(quote: Char, template: Boolean) {
        var c: Char
        val sb = StringBuilder()
        loop@while (true) {
            c = this.advance()
            when (c) {
                0.toChar(), '\r', '\n' -> throw SyntaxException("Unterminated string.")
                '\\' -> {
                    c = this.advance()
                    when (c) {
                        'b' -> sb.append('\b')
                        't' -> sb.append('\t')
                        'n' -> sb.append('\n')
                        //'f' -> sb.append('\f')
                        'r' -> sb.append('\r')
                        'u' -> try {
                            sb.append(Integer.parseInt(this.advance(4), 16).toChar())
                        } catch (e: NumberFormatException) {
                            throw SyntaxException("Illegal escape.", e)
                        }

                        '"', '\'', '\\', '/' -> sb.append(c)
                        else -> throw SyntaxException("Illegal escape.")
                    }
                }
                '$' // String templates
                -> {
                    if (template) {
                        if (Character.isLetter(peek())) {
                            push(make(TokenType.STRING, sb.toString()))
                            sb.setLength(0)
                            push(make(TokenType.PLUS))
                            readName(advance())

                            if (peek() != quote) {
                                push(make(TokenType.PLUS))
                            } else {
                                advance()
                                return
                            }
                            break@loop
                        } else if (peek() == '{') {
                            push(make(TokenType.STRING, sb.toString()))
                            sb.setLength(0)
                            push(make(TokenType.PLUS))

                            advance()
                            while (peek() != '}') {
                                if (!hasNext()) {
                                    throw SyntaxException("Unterminated template.")
                                }
                                sb.append(advance())
                            }
                            advance()

                            tokens.add(make(TokenType.LEFT_PAREN))
                            tokens.addAll(KaiperLexer(sb.toString()).tokens)
                            if (lastToken().type != TokenType.EOF) {
                                throw SyntaxException("Internal error")
                            }
                            tokens.removeAt(tokens.size - 1)
                            tokens.add(make(TokenType.RIGHT_PAREN))
                            sb.setLength(0)

                            if (peek() != quote) {
                                push(make(TokenType.PLUS))
                            } else {
                                advance()
                                return
                            }

                            break@loop
                        }
                        sb.append(c)
                        break@loop
                    }
                    if (c == quote) {
                        val token = make(TokenType.STRING, sb.toString())
                        push(token)
                        return
                    }
                    sb.append(c)
                }
                else -> {
                    if (c == quote) {
                        val token = make(TokenType.STRING, sb.toString())
                        push(token)
                        return
                    }
                    sb.append(c)
                }
            }
        }
    }

    private fun make(type: TokenType): Token {
        return make(Position(index, line, lineIndex), type)
    }

    private fun make(type: TokenType, value: String): Token {
        return make(Position(index - value.length, line, lineIndex - value.length), type, value)
    }

    private fun make(position: Position, type: TokenType): Token {
        return Token(position, type)
    }

    private fun make(position: Position, type: TokenType, value: String): Token {
        return Token(position, type, value)
    }
}