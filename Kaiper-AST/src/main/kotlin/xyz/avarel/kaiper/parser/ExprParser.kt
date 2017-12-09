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

import xyz.avarel.kaiper.Precedence
import xyz.avarel.kaiper.ast.expr.Expr
import xyz.avarel.kaiper.ast.expr.flow.Statements
import xyz.avarel.kaiper.ast.expr.value.NullNode
import xyz.avarel.kaiper.ast.expr.variables.Identifier
import xyz.avarel.kaiper.ast.pattern.PatternCase
import xyz.avarel.kaiper.exceptions.SyntaxException
import xyz.avarel.kaiper.lexer.KaiperLexer
import xyz.avarel.kaiper.lexer.Token
import xyz.avarel.kaiper.lexer.TokenType
import java.util.*

open class ExprParser : Parser {
    private val patternParser: PatternParser by lazy {
        PatternParser(this)
    }

    constructor(tokens: KaiperLexer) : super(tokens, DefaultGrammar.INSTANCE)
    constructor(proxy: ExprParser) : super(proxy)

    fun parsePattern(): PatternCase {
        return patternParser.parsePatternCase()
    }

    fun parse(): Expr {
        val expr = parseStatements()

        if (!getTokens().isEmpty()) {
            val t = getTokens()[0]
            if (t.type !== TokenType.EOF) {
                throw SyntaxException("Unexpected " + t, t.position)
            }
        }

        return expr
    }

    fun parseStatements(): Expr {
        val exprList = ArrayList<Expr>()

        do {
            if (match(TokenType.EOF)) break
            exprList.add(parseExpr())
        } while (match(TokenType.LINE))

        return if (exprList.size == 1) exprList[0] else Statements(exprList)
    }

    fun parseIdentifier(): Identifier {
        val position = peek(0).position
        val expr = parseExpr(Precedence.DOT - 1)
        return expr as? Identifier ?: throw SyntaxException("Expected IDENTIFIER", position)
    }

    fun parseBlock(): Expr {
        var expr: Expr = NullNode.VALUE
        eat(TokenType.LEFT_BRACE)
        if (!match(TokenType.RIGHT_BRACE)) {
            expr = parseStatements()
            eat(TokenType.RIGHT_BRACE)
        }
        return expr
    }

    fun parseExpr(precedence: Int = 0): Expr {
        val token = eat()

        val expr = parsePrefix(token)

        return parseInfix(precedence, expr)
    }

    fun parsePrefix(token: Token): Expr {
        val prefix = prefixParsers[token.type] ?: throw SyntaxException("Unexpected " + token, token.position)

        return prefix.parse(this, token)
    }

    open fun parseInfix(precedence: Int, left: Expr): Expr {
        var leftVar = left
        while (precedence < precedence) {
            val token = eat()

            val infix = infixParsers[token.type]

            if (infix == null) {
                throw SyntaxException("Unexpected " + token, token.position)
            } else {
                leftVar = infix.parse(this, leftVar, token)
            }
        }
        return leftVar
    }

    fun eatSoftKeyword(keyword: String) {
        val token: Token
        if (eat().string != keyword) {
            token = last
            throw SyntaxException("Expected `$keyword`", token.position)
        }
    }

    fun matchSoftKeyword(keyword: String): Boolean {
        if (peek(0).string == keyword) {
            eat()
            return true
        }
        return false
    }
}
