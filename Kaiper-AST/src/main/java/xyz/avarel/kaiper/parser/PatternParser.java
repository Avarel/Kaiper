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

package xyz.avarel.kaiper.parser;

import xyz.avarel.kaiper.Precedence;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.ast.pattern.*;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class PatternParser extends KaiperParser {
    private boolean requireName;
    private boolean requireDef;
    private boolean rest;

    // proxy the parser
    public PatternParser(KaiperParser parser) {
        super(parser);
    }

    public PatternCase parsePatternCase() {
        List<Pattern> patterns = new ArrayList<>();

        do {
            patterns.add(parsePattern());
        } while (match(TokenType.COMMA));

        return new PatternCase(patterns);
    }

    private Pattern parsePattern() {
        Pattern basePattern;

        if (nextIs(TokenType.IDENTIFIER)) {
            requireName = true;
            String name = eat(TokenType.IDENTIFIER).getString();

            if (match(TokenType.COLON)) {
                Pattern simplePattern;

                if (nextIs(TokenType.IDENTIFIER)) {
                    String innerName = eat(TokenType.IDENTIFIER).getString();
                    simplePattern = new VariablePattern(innerName);
                } else {
                    simplePattern = parseSimplePattern();
                }

                basePattern = new TuplePattern(name, simplePattern);
            } else { // x ~= _0: x
                basePattern = new VariablePattern(name);
            }

            if (match(TokenType.ASSIGN)) {
                requireDef = true;

                Single def = parseSingle(Precedence.TUPLE_PAIR);
                basePattern = new DefaultPattern((NamedPattern) basePattern, def);
            } else if (requireDef) {
                throw new SyntaxException("All parameters after the first default requires a default",
                        peek(0).getPosition());
            }
        } else if (match(TokenType.REST)) {
            if (rest) {
                throw new SyntaxException("Can only have 1 rest pattern", getLast().getPosition());
            }

            requireName = true;
            rest = true;

            String name = eat(TokenType.IDENTIFIER).getString();

            basePattern = new RestPattern(name);
        } else {
            if (requireName) {
                throw new SyntaxException("Can not mix positional and named patterns", peek(0).getPosition());
            }
            basePattern = parseSimplePattern();
        }

        return basePattern;
    }

    private Pattern parseSimplePattern() {
        if (nextIsAny(
                TokenType.INT, TokenType.NUMBER, TokenType.STRING,
                TokenType.NULL, TokenType.BOOLEAN, TokenType.LEFT_BRACKET
        ) || match(TokenType.EQUALS)) {
            Single value = parseSingle(Precedence.TUPLE);
            return new ValuePattern(value);
        } else if (match(TokenType.UNDERSCORE)) {
            return WildcardPattern.INSTANCE;
        } else if (match(TokenType.LEFT_PAREN)) {
            PatternCase nested = parsePatternCase();
            eat(TokenType.RIGHT_PAREN);
            return nested;
        } else {
            Token unexpected = peek(0);
            throw new SyntaxException("Unexpected pattern " + unexpected.getType(), unexpected.getPosition());
        }
    }
}
