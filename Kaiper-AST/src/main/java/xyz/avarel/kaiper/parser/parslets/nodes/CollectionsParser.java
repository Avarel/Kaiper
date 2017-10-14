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

package xyz.avarel.kaiper.parser.parslets.nodes;

import xyz.avarel.kaiper.Precedence;
import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.collections.ArrayNode;
import xyz.avarel.kaiper.ast.collections.DictionaryNode;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.KaiperParser;
import xyz.avarel.kaiper.parser.PrefixParser;

import java.util.*;

public class CollectionsParser implements PrefixParser {
    @Override
    public Expr parse(KaiperParser parser, Token token) {
        // EMPTY DICTIONARY
        if (parser.match(TokenType.COLON)) {
            parser.eat(TokenType.RIGHT_BRACKET);
            return new DictionaryNode(token.getPosition(), Collections.emptyMap());
        }

        // EMPTY ARRAY
        if (parser.match(TokenType.RIGHT_BRACKET)) {
            return new ArrayNode(token.getPosition(), Collections.emptyList());
        }

        Expr expr = parser.parseExpr(Precedence.TUPLE);

        return parser.match(TokenType.COLON)
                ? parseDictionary(parser, token, expr)
                : parseVector(parser, token, expr);
    }

    private Expr parseVector(KaiperParser parser, Token token, Expr initItem) {
        List<Expr> items = new ArrayList<>();

        items.add(initItem);

        while (parser.match(TokenType.COMMA)) {
            items.add(parser.parseExpr(Precedence.TUPLE));
        }

        parser.eat(TokenType.RIGHT_BRACKET);

        return new ArrayNode(token.getPosition(), items);
    }

    private Expr parseDictionary(KaiperParser parser, Token token, Expr initKey) {
        Map<Expr, Expr> map = new HashMap<>();

        map.put(initKey, parser.parseExpr(Precedence.TUPLE));

        while (parser.match(TokenType.COMMA)) {
            Expr key = parser.parseExpr(Precedence.TUPLE);
            parser.eat(TokenType.COLON);
            Expr value = parser.parseExpr(Precedence.TUPLE);

            map.put(key, value);
        }

        parser.eat(TokenType.RIGHT_BRACKET);

        return new DictionaryNode(token.getPosition(), map);
    }
}
