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

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.ast.collections.ArrayNode;
import xyz.avarel.kaiper.ast.collections.DictionaryNode;
import xyz.avarel.kaiper.ast.collections.GetOperation;
import xyz.avarel.kaiper.ast.flow.Statements;
import xyz.avarel.kaiper.ast.value.IntNode;
import xyz.avarel.kaiper.ast.value.StringNode;
import xyz.avarel.kaiper.ast.variables.AssignmentExpr;
import xyz.avarel.kaiper.ast.variables.DeclarationExpr;
import xyz.avarel.kaiper.ast.variables.Identifier;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.AJEParser;
import xyz.avarel.kaiper.parser.PrefixParser;

import java.util.*;

public class CollectionsParser implements PrefixParser {
    @Override
    public Expr parse(AJEParser parser, Token token) {
        // EMPTY DICTIONARY
        if (parser.match(TokenType.COLON)) {
            if (!parser.getParserFlags().allowDictionary()) {
                throw new SyntaxException("Dictionaries are disabled");
            }
            parser.eat(TokenType.RIGHT_BRACKET);
            return new DictionaryNode(Collections.emptyMap());
        }

        // EMPTY ARRAY
        if (parser.match(TokenType.RIGHT_BRACKET)) {
            if (!parser.getParserFlags().allowArray()) {
                throw new SyntaxException("Arrays are disabled");
            }
            return new ArrayNode(Collections.emptyList());
        }

        Single expr = parser.parseSingle();

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

    private Expr parseVector(AJEParser parser, Token token, Single initItem) {
        List<Single> items = new ArrayList<>();

        items.add(initItem);

        while (parser.match(TokenType.COMMA)) {
            items.add(parser.parseSingle());
        }

        parser.eat(TokenType.RIGHT_BRACKET);

        if (parser.match(TokenType.ASSIGN)) {
            for (Expr expr : items) {
                if (!(expr instanceof Identifier)) {
                    throw new SyntaxException("Invalid left-hand side in assignment expression", token.getPosition());
                }
            }

            Single target = parser.parseSingle();

            Statements statements = new Statements();

            statements.getExprs().add(new DeclarationExpr("$TEMP", target));

            for (int i = 0; i < items.size(); i++) {
                Expr expr = items.get(i);
                Identifier id = (Identifier) expr;
                statements.getExprs().add(
                        new AssignmentExpr(
                                id.getParent(),
                                id.getName(),
                                new GetOperation(new Identifier("$TEMP"), new IntNode(i))
                        )
                );
            }

            statements.getExprs().add(new Identifier("$TEMP"));

            return statements;
        }

        return new ArrayNode(items);
    }

    private Expr parseDictionary(AJEParser parser, Token token, Single initKey) {
        Map<Single, Single> map = new HashMap<>();

        if (initKey instanceof Identifier) {
            initKey = new StringNode(((Identifier) initKey).getName());
        }

        map.put(initKey, parser.parseSingle());

        while (parser.match(TokenType.COMMA)) {
            Single key = parser.parseSingle();
            parser.eat(TokenType.COLON);
            Single value = parser.parseSingle();

            map.put(key, value);
        }

        parser.eat(TokenType.RIGHT_BRACKET);

        return new DictionaryNode(map);
    }
}
