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
import xyz.avarel.kaiper.ast.pattern.*
import xyz.avarel.kaiper.exceptions.SyntaxException
import xyz.avarel.kaiper.lexer.TokenType
import java.util.*

class PatternParser(parser: ExprParser) : ExprParser(parser) {
    fun parsePatternCase(patterns: MutableList<Pattern> = ArrayList(), context: ParseContext = ParseContext()): PatternCase {
        do {
            patterns.add(parsePattern(context))
        } while (match(TokenType.COMMA))

        return PatternCase(patterns)
    }

    private fun parsePattern(context: ParseContext): Pattern {
        when {
            nextIs(TokenType.IDENTIFIER) -> {
                val token = eat()
                val name = token.string

                if (context.usedIdentifiers.contains(name)) {
                    throw SyntaxException("Duplicate argument name", token.position)
                } else {
                    context.usedIdentifiers.add(name)
                }

                val pattern = VariablePattern(name)

                return if (match(TokenType.ASSIGN)) {
                    DefaultPattern(pattern, parseExpr(Precedence.FREEFORM_STRUCT))
                } else pattern

            }
            match(TokenType.REST) -> {
                if (context.restPattern) {
                    throw SyntaxException("Only one rest argument allowed")
                }

                val token = eat()
                val name = token.string

                if (context.usedIdentifiers.contains(name)) {
                    throw SyntaxException("Duplicate argument name", token.position)
                } else {
                    context.usedIdentifiers.add(name)
                }

                context.restPattern = true

                return RestPattern(name)
            }
            match(TokenType.LEFT_PAREN) -> {
                if (match(TokenType.RIGHT_PAREN)) {
                    return NestedPattern(PatternCase.EMPTY)
                }

                val patternCase = parsePatternCase(ArrayList(), context)

                eat(TokenType.RIGHT_PAREN)

                return NestedPattern(patternCase)
            }
            match(TokenType.UNDERSCORE) -> return WildcardPattern.INSTANCE
            else -> {
                match(TokenType.EQUALS)
                return ValuePattern(parseExpr(Precedence.FREEFORM_STRUCT))
            }
        }
    }

    data class ParseContext(val usedIdentifiers: MutableSet<String> = HashSet(), var restPattern: Boolean = false)
}