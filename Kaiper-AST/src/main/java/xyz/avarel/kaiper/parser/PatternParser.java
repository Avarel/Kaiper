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
import xyz.avarel.kaiper.ast.pattern.*;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PatternParser extends KaiperParser {
    private final KaiperParser parser;
    private final Set<String> usedIdentifiers;

    public PatternParser(KaiperParser parser) {
        super(parser);
        this.parser = parser;
        this.usedIdentifiers = new HashSet<>();
    }

    public PatternCase parsePatternCase() {
        return parsePatternCase(new ArrayList<>());
    }

    public PatternCase parsePatternCase(List<Pattern> patterns) {
        do {
            patterns.add(parsePattern());
        } while (match(TokenType.COMMA));

        return new PatternCase(patterns);
    }

    private Pattern parsePattern() {
        if (nextIs(TokenType.IDENTIFIER)) {
            Token token = eat(TokenType.IDENTIFIER);
            String name = token.getString();

            if (usedIdentifiers.contains(name)) {
                throw new SyntaxException("Duplicate pattern name", token.getPosition());
            } else {
                usedIdentifiers.add(name);
            }

//            if (match(TokenType.COLON)) {
//                if (match(TokenType.LEFT_PAREN)) {
//                    basePattern = new NestedPattern(name, new PatternParser(parser).parsePatternCase());
//                    eat(TokenType.RIGHT_PAREN);
//                } else {
//                    basePattern = new TuplePattern(name, parseExpr());
//                }
//            } else {
            VariablePattern pattern = new VariablePattern(name);

            if (match(TokenType.ASSIGN)) {
                return new DefaultPattern(pattern, parseExpr());
            }

            return pattern;
        } else if (match(TokenType.LEFT_PAREN)) {
            PatternCase patternCase = parsePatternCase();

            eat(TokenType.RIGHT_PAREN);

            if (patternCase.size() < 2) {
                throw new SyntaxException("Nested patterns needs to have 2 or more parameters");
            }

            return new NestedPattern(patternCase);
        } else {
            match(TokenType.EQUALS);
            return new ValuePattern(parseExpr(Precedence.FREEFORM_STRUCT));
        }
    }
}
