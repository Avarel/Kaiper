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
import java.io.*
import java.util.*

abstract class Lexer<T> @JvmOverloads constructor(reader: Reader, historyBuffer: Int = 2) {
    protected val tokens: MutableList<T> = LinkedList()

    protected val reader: Reader
    protected val history: Array<Entry?>
    protected var previous: Int = 0
    protected var eof: Boolean = false

    protected var lineIndex: Long = 0
    protected var index: Long = 0
    protected var line: Long = 0
    protected var current: Char = ' '

    /**
     * Return the current xyz.avarel.kaiper.lexer's positional data.
     *
     * @return A plain object with position data.
     */
    val position: Position
        get() = Position(index, line, lineIndex)

    constructor(inputStream: InputStream) : this(InputStreamReader(inputStream))

    constructor(s: String) : this(StringReader(s))

    init {
        this.eof = false
        this.reader = if (reader.markSupported()) reader else BufferedReader(reader)
        history = arrayOfNulls(historyBuffer)

        this.current = 0.toChar()
        this.index = -1
        this.lineIndex = 0
        this.line = 1

        beforeReading()

        do {
            readTokens()
        } while (hasNext())

        afterReading()

        try {
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * Make a printable string of this KaiperLexer.
     *
     * @return " at {index} [{character} : {line}]"
     */
    override fun toString(): String {
        return position.toString()
    }

    protected abstract fun readTokens()

    /**
     * Get and remove a token from the tokens list.
     *
     * @return The next token.
     */
    operator fun next(): T {
        return tokens.removeAt(0)
    }

    protected fun push(token: T?) {
        if (token == null) throw IllegalArgumentException()
        tokens.add(token)
    }

    protected fun lastToken(): T {
        return tokens[tokens.size - 1]
    }

    protected fun beforeReading() {}

    protected fun afterReading() {}

    operator fun hasNext(): Boolean {
        return !(previous == 0 && this.eof)
    }

    // useful for xyz.avarel.kaiper.lexer-phase desugaring
    protected fun queue(character: Char) {
        System.arraycopy(history, previous + 1, history, 3, history.size - 3)
        history[previous] = Entry(index, line, lineIndex, character)
        back()
    }

    /**
     * Get the readToken character in the source string.
     *
     * @return The readToken character, or 0 if past the end of the source string.
     */
    protected fun advance(): Char {
        var c: Int
        if (this.previous != 0) {
            this.previous--

            val entry = history[previous]!!

            current = entry.character
            index = entry.index
            line = entry.line
            lineIndex = entry.lineIndex

            return this.current
        } else {
            try {
                c = this.reader.read()
            } catch (exception: IOException) {
                throw SyntaxException("Exception occurred while lexing", position, exception)
            }

            if (c <= 0) { // End of stream
                this.eof = true
                c = 0
            }
        }

        this.index += 1
        when {
            this.current == '\r' -> {
                this.line += 1
                this.lineIndex = (if (c.toChar() == '\n') 0 else 1).toLong()
            }
            c.toChar() == '\n' -> {
                this.line += 1
                this.lineIndex = 0
            }
            else -> this.lineIndex += 1
        }
        this.current = c.toChar()

        System.arraycopy(history, 0, history, 1, history.size - 1)

        history[0] = Entry(index, line, lineIndex, current)

        return this.current
    }

    /**
     * Consume the next character, and check that
     * it matches a specified character.
     *
     * @param c The character to match.
     * @return The character.
     */
    private fun advance(c: Char): Char {
        val n = this.advance()
        if (n != c) {
            throw SyntaxException("Expected '$c' and instead saw '$n'", position)
        }
        return n
    }

    /**
     * Get the next n characters.
     *
     * @param n The number of characters to take.
     * @return A string of n characters.
     * @throws SyntaxException Substring bounds error if there are not
     * n characters remaining in the source string.
     */
    protected fun advance(n: Int): String {
        if (n == 0) {
            return ""
        }

        val chars = CharArray(n)
        var pos = 0

        while (pos < n) {
            chars[pos] = this.advance()
            if (!this.hasNext()) {
                throw SyntaxException("Substring bounds error", position)
            }
            pos += 1
        }
        return String(chars)
    }

    /**
     * Get the next char in the string, skipping whitespace.
     *
     * @return A character, or 0 if there are no more characters.
     */
    private fun advanceClean(): Char {
        while (true) {
            val c = this.advance()
            if (c.toInt() == 0 || c > ' ') {
                return c
            }
        }
    }

    /**
     * @return The next character.
     */
    protected fun peek(): Char {
        val c = advance()
        back()
        return c
    }

    /**
     * Peek and advance if the prompt is the same as the peeked character.
     *
     * @param prompt The character to match.
     * @return if the prompt is the same as the peeked character.
     */
    protected fun match(prompt: Char): Boolean {
        if (advance() == prompt) {
            return true
        }
        back()
        return false
    }

    /**
     * Back up one character. This provides a sort of lookahead capability,
     * so that you can test for a digit or letter before attempting to parse
     * the readToken number or identifier.
     */
    protected fun back() {
        previous++
    }

    /**
     * Get the text up but not including the specified character or the
     * end of line, whichever comes first.
     *
     * @param delimiter A delimiter character.
     * @return A string.
     */
    private fun advanceTo(delimiter: Char): String {
        val sb = StringBuilder()
        while (true) {
            val c = this.advance()
            if (c == delimiter || c.toInt() == 0 || c == '\n' || c == '\r') {
                if (c.toInt() != 0) {
                    this.back()
                }
                return sb.toString().trim { it <= ' ' }
            }
            sb.append(c)
        }
    }

    /**
     * Get the text up but not including one of the specified delimiter
     * characters or the end of line, whichever comes first.
     *
     * @param delimiters A set of delimiter characters.
     * @return A string, trimmed.
     */
    private fun advanceTo(delimiters: String): String {
        var c: Char
        val sb = StringBuilder()
        while (true) {
            c = this.advance()
            if (delimiters.indexOf(c) >= 0 || c.toInt() == 0 ||
                    c == '\n' || c == '\r') {
                if (c.toInt() != 0) {
                    this.back()
                }
                return sb.toString().trim { it <= ' ' }
            }
            sb.append(c)
        }
    }

    /**
     * Skip characters until the readToken character is the requested character.
     * If the requested character is not found, no characters are skipped.
     *
     * @param to A character to skip to.
     * @return The requested character, or zero if the requested character
     * is not found.
     */
    private fun skipTo(to: Char): Char {
        var c: Char
        try {
            val startIndex = this.index
            val startCharacter = this.lineIndex
            val startLine = this.line
            this.reader.mark(1000000)
            do {
                c = this.advance()
                if (c.toInt() == 0) {
                    this.reader.reset()
                    this.index = startIndex
                    this.lineIndex = startCharacter
                    this.line = startLine
                    return c
                }
            } while (c != to)
        } catch (exception: IOException) {
            throw SyntaxException("Exception occurred while lexing", position, exception)
        }

        this.back()
        return c
    }

    protected class Entry(val index: Long, val line: Long, val lineIndex: Long, val character: Char) {
        override fun toString(): String {
            return character.toString()
        }
    }
}
