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

package xyz.avarel.kaiper.parser.parslets.nodes

import xyz.avarel.kaiper.Precedence
import xyz.avarel.kaiper.ast.expr.Expr
import xyz.avarel.kaiper.ast.expr.collections.ArrayNode
import xyz.avarel.kaiper.ast.expr.collections.DictionaryNode
import xyz.avarel.kaiper.lexer.Token
import xyz.avarel.kaiper.lexer.TokenType
import xyz.avarel.kaiper.parser.ExprParser
import xyz.avarel.kaiper.parser.PrefixParser

import java.util.*

class CollectionsParser : PrefixParser {
    override fun parse(parser: ExprParser, token: Token): Expr {
        // EMPTY DICTIONARY
        if (parser.match(TokenType.COLON)) {
            parser.eat(TokenType.RIGHT_BRACKET)
            return DictionaryNode(token.position, emptyMap())
        }

        // EMPTY ARRAY
        if (parser.match(TokenType.RIGHT_BRACKET)) {
            return ArrayNode(token.position, emptyList())
        }

        val expr = parser.parseExpr(Precedence.TUPLE)

        return if (parser.match(TokenType.COLON))
            parseDictionary(parser, token, expr)
        else
            parseVector(parser, token, expr)
    }

    private fun parseVector(parser: ExprParser, token: Token, initItem: Expr): Expr {
        val items = ArrayList<Expr>()

        items.add(initItem)

        while (parser.match(TokenType.COMMA)) {
            items.add(parser.parseExpr(Precedence.TUPLE))
        }

        parser.eat(TokenType.RIGHT_BRACKET)

        return ArrayNode(token.position, items)
    }

    private fun parseDictionary(parser: ExprParser, token: Token, initKey: Expr): Expr {
        val map = HashMap<Expr, Expr>()

        map.put(initKey, parser.parseExpr(Precedence.TUPLE))

        while (parser.match(TokenType.COMMA)) {
            val key = parser.parseExpr(Precedence.TUPLE)
            parser.eat(TokenType.COLON)
            val value = parser.parseExpr(Precedence.TUPLE)

            map.put(key, value)
        }

        parser.eat(TokenType.RIGHT_BRACKET)

        return DictionaryNode(token.position, map)
    }
}
