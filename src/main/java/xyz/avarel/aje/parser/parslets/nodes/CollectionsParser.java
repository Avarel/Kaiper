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

package xyz.avarel.aje.parser.parslets.nodes;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ValueNode;
import xyz.avarel.aje.ast.collections.DictionaryNode;
import xyz.avarel.aje.ast.collections.VectorNode;
import xyz.avarel.aje.ast.variables.Identifier;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.PrefixParser;
import xyz.avarel.aje.parser.lexer.Token;
import xyz.avarel.aje.parser.lexer.TokenType;
import xyz.avarel.aje.runtime.Str;

import java.util.*;

public class CollectionsParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {

        // EMPTY DICTIONARY
        if (parser.match(TokenType.COLON)) {
            parser.eat(TokenType.RIGHT_BRACKET);
            return new DictionaryNode(token.getPosition(), Collections.emptyMap());
        }

        if (parser.match(TokenType.RIGHT_BRACKET)) {
            return new VectorNode(token.getPosition(), Collections.emptyList());
        }

        Expr expr = parser.parseExpr();

        if (parser.match(TokenType.COLON)) {
            return parseDictionary(parser, token, expr);
        } else {
            return parseVector(parser, token, expr);
        }
    }

    private Expr parseVector(AJEParser parser, Token token, Expr initItem) {
        List<Expr> items = new ArrayList<>();

        items.add(initItem);

        while (parser.match(TokenType.COMMA)) {
            items.add(parser.parseExpr());
        }

        parser.eat(TokenType.RIGHT_BRACKET);

        return new VectorNode(token.getPosition(), items);
    }

    private Expr parseDictionary(AJEParser parser, Token token, Expr initKey) {
        Map<Expr, Expr> map = new HashMap<>();

        if (initKey instanceof Identifier) {
            initKey = new ValueNode(initKey.getPosition(), Str.of(((Identifier) initKey).getName()));
        }

        map.put(initKey, parser.parseExpr());

        while (parser.match(TokenType.COMMA)) {
            Expr key = parser.parseExpr();
            parser.eat(TokenType.COLON);
            Expr value = parser.parseExpr();

            map.put(key, value);
        }

        parser.eat(TokenType.RIGHT_BRACKET);

        return new DictionaryNode(token.getPosition(), map);
    }
}
