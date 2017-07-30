/*
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package xyz.avarel.kaiper.parser.parslets.nodes;

import xyz.avarel.kaiper.Precedence;
import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.ast.collections.ArrayNode;
import xyz.avarel.kaiper.ast.collections.DictionaryNode;
import xyz.avarel.kaiper.exceptions.SyntaxException;
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
            if (!parser.getParserFlags().allowDictionary()) {
                throw new SyntaxException("Dictionaries are disabled");
            }
            parser.eat(TokenType.RIGHT_BRACKET);
            return new DictionaryNode(token.getPosition(), Collections.emptyMap());
        }

        // EMPTY ARRAY
        if (parser.match(TokenType.RIGHT_BRACKET)) {
            if (!parser.getParserFlags().allowArray()) {
                throw new SyntaxException("Arrays are disabled");
            }
            return new ArrayNode(token.getPosition(), Collections.emptyList());
        }

        Single expr = parser.parseSingle(Precedence.TUPLE_PAIR);

        if (parser.match(TokenType.COLON)) {
            if (!parser.getParserFlags().allowDictionary()) {
                throw new SyntaxException("Dictionaries are disabled");
            }
            return parseDictionary(parser, token, expr);
        } else {
            if (!parser.getParserFlags().allowArray()) {
                throw new SyntaxException("Arrays are disabled");
            }
            return parseVector(parser, token, expr);
        }
    }

    private Expr parseVector(KaiperParser parser, Token token, Single initItem) {
        List<Single> items = new ArrayList<>();

        items.add(initItem);

        while (parser.match(TokenType.COMMA)) {
            items.add(parser.parseSingle(Precedence.TUPLE_PAIR));
        }

        parser.eat(TokenType.RIGHT_BRACKET);

        return new ArrayNode(token.getPosition(), items);
    }

    private Expr parseDictionary(KaiperParser parser, Token token, Single initKey) {
        Map<Single, Single> map = new HashMap<>();

        map.put(initKey, parser.parseSingle(Precedence.TUPLE_PAIR));

        while (parser.match(TokenType.COMMA)) {
            Single key = parser.parseSingle(Precedence.TUPLE_PAIR);
            parser.eat(TokenType.COLON);
            Single value = parser.parseSingle(Precedence.TUPLE_PAIR);

            map.put(key, value);
        }

        parser.eat(TokenType.RIGHT_BRACKET);

        return new DictionaryNode(token.getPosition(), map);
    }
}
