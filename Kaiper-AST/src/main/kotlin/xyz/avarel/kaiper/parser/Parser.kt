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

package xyz.avarel.kaiper.parser

import xyz.avarel.kaiper.exceptions.SyntaxException
import xyz.avarel.kaiper.lexer.KaiperLexer
import xyz.avarel.kaiper.lexer.Position
import xyz.avarel.kaiper.lexer.Token
import xyz.avarel.kaiper.lexer.TokenType
import java.util.*

abstract class Parser {
    val lexer: KaiperLexer
    private val tokens: MutableList<Token>
    private val grammar: Grammar

    lateinit var last: Token

    val prefixParsers: Map<TokenType, PrefixParser>
        get() = grammar.prefixParsers

    val infixParsers: Map<TokenType, InfixParser>
        get() = grammar.infixParsers

    val precedence: Int
        get() {
            val parser = grammar.infixParsers[peek(0).type]
            return parser?.precedence ?: 0

        }

    val position: Position
        get() = last.position

    @JvmOverloads constructor(lexer: KaiperLexer, grammar: Grammar = Grammar()) {
        this.lexer = lexer
        this.tokens = ArrayList()
        this.grammar = grammar
    }

    protected constructor(proxy: Parser) {
        this.lexer = proxy.lexer
        this.tokens = proxy.tokens
        this.grammar = proxy.grammar
    }

    fun getTokens(): List<Token> {
        return tokens
    }

    fun pushToken(token: Token) {
        tokens.add(0, token)
    }

    fun match(expected: TokenType): Boolean {
        val token = peek(0)
        if (token.type !== expected) {
            return false
        }
        eat()
        return true
    }

    fun matchSignificant(expected: TokenType): Boolean {
        val token = peekSignificant(0)
        if (token!!.type !== expected) {
            return false
        }
        eatSignificant()
        return true
    }

    fun matchAny(vararg tokens: TokenType): Boolean {
        val token = peek(0)
        for (expected in tokens) {
            if (token.type === expected) {
                eat()
                return true
            }
        }
        return false
    }

    open fun eat(): Token {
        // Make sure we've read the token.
        peek(0)

        tokens.removeAt(0).let {
            last = it
            return it
        }
    }

    fun eat(expected: TokenType): Token {
        val token = peek(0)
        if (token.type !== expected) {
            throw SyntaxException("Expected token " + expected + " but found " + token.type, token.position)
        }
        return eat()
    }

    fun eatSignificant(): Token? {
        var token: Token? = null
        while (!eat().type.isSignificant) {
            token = last
        }
        return token
    }

    fun peek(distance: Int): Token {
        // Read in as many as needed.
        while (distance >= tokens.size) {
            tokens.add(lexer.next())
        }

        // Get the queued token.
        return tokens[distance]
    }

    fun peekSignificant(distance: Int): Token? {
        var i = 0
        var peek = 0
        var token: Token? = null
        while (i <= distance) {
            token = peek(peek)
            if (token.type.isSignificant) {
                i++
            }
            peek++
        }
        return token
    }

    fun nextIs(vararg tokens: TokenType): Boolean {
        for (i in tokens.indices) {
            if (peek(i).type !== tokens[i]) return false
        }

        return true
    }

    fun nextIsAny(vararg tokens: TokenType): Boolean {
        for (token in tokens) {
            if (this.nextIs(token)) return true
        }

        return false
    }

    fun nextIs(type: TokenType): Boolean {
        return peek(0).type === type
    }
}
