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

package xyz.avarel.kaiper.parser.parslets

import xyz.avarel.kaiper.ast.expr.Expr
import xyz.avarel.kaiper.ast.expr.TypeNode
import xyz.avarel.kaiper.ast.pattern.PatternCase
import xyz.avarel.kaiper.lexer.Token
import xyz.avarel.kaiper.lexer.TokenType
import xyz.avarel.kaiper.parser.ExprParser
import xyz.avarel.kaiper.parser.PrefixParser

/**
 * type IDENTIFIER(params...)
 */

class TypeParser : PrefixParser {
    override fun parse(parser: ExprParser, token: Token): Expr {
        val name = parser.eat(TokenType.IDENTIFIER).string

        var patternCase = PatternCase.EMPTY
        if (parser.match(TokenType.LEFT_PAREN)) {
            patternCase = parser.parsePattern()
            parser.eat(TokenType.RIGHT_PAREN)
        }

        return TypeNode(token.position, name, patternCase)
    }
}
