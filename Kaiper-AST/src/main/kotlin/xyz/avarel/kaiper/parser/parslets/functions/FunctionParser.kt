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

package xyz.avarel.kaiper.parser.parslets.functions

import xyz.avarel.kaiper.ast.expr.Expr
import xyz.avarel.kaiper.ast.expr.functions.FunctionNode
import xyz.avarel.kaiper.ast.expr.variables.Identifier
import xyz.avarel.kaiper.ast.pattern.Pattern
import xyz.avarel.kaiper.ast.pattern.PatternCase
import xyz.avarel.kaiper.ast.pattern.VariablePattern
import xyz.avarel.kaiper.exceptions.SyntaxException
import xyz.avarel.kaiper.lexer.Token
import xyz.avarel.kaiper.lexer.TokenType
import xyz.avarel.kaiper.parser.ExprParser
import xyz.avarel.kaiper.parser.PatternParser
import xyz.avarel.kaiper.parser.PrefixParser
import java.util.*

class FunctionParser : PrefixParser {
    override fun parse(parser: ExprParser, token: Token): Expr {
        if (parser.match(TokenType.UNDERSCORE)) {
            return parseUnderscore(parser, parser.last)
        }

        var name: String? = null
        if (parser.match(TokenType.IDENTIFIER)) {
            name = parser.last.string
        }

        val patternCase: PatternCase
        parser.eat(TokenType.LEFT_PAREN)
        if (!parser.match(TokenType.RIGHT_PAREN)) {
            patternCase = PatternParser(parser).parsePatternCase()
            parser.eat(TokenType.RIGHT_PAREN)
        } else {
            patternCase = PatternCase.EMPTY
        }

        val expr: Expr

        if (parser.match(TokenType.ASSIGN)) {
            expr = parser.parseExpr()
        } else {
            expr = parser.parseBlock()
        }

        return FunctionNode(token.position, name, patternCase, expr)
    }

    private fun parseUnderscore(parser: ExprParser, token: Token): Expr {
        val ip = ParserProxy(parser, token)

        val expr = ip.parseInfix(0, Identifier(token.position, token.string))

        val list = ArrayList<Pattern>()

        for (param in ip.parameters) {
            list.add(VariablePattern(param))
        }

        return FunctionNode(token.position, PatternCase(list), expr)
    }

    private class ParserProxy(private val proxy: ExprParser, token: Token) : ExprParser(proxy) {
        val parameters = LinkedHashSet<String>()
        var current: ExprParser? = null

        init {
            this.current = this
            this.parameters.add(token.string)
        }

        override fun eat(): Token {
            val token = super.eat()
            if (token.type === TokenType.UNDERSCORE) {
                parameters.add(token.string)
                return Token(token.position, TokenType.IDENTIFIER, token.string)
            }
            return token
        }

        override fun parseInfix(precedence: Int, left: Expr): Expr {
            var leftVar = left
            while (precedence < precedence) {
                val token = eat()

                if (token.type === TokenType.PIPE_FORWARD) {
                    current = proxy
                }

                val infix = infixParsers[token.type] ?: throw SyntaxException("Could not parse token `" + token.string + "`", token.position)

                leftVar = infix.parse(current!!, leftVar, token)
            }
            return leftVar
        }
    }
}
