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
import java.util.concurrent.atomic.AtomicBoolean;

public class PatternParser extends ExprParser {

    public PatternParser(ExprParser parser) {
        super(parser);
    }

    public PatternCase parsePatternCase() {
        return parsePatternCase(new ArrayList<>());
    }

    public PatternCase parsePatternCase(List<Pattern> patterns) {
        Set<String> usedIdentifiers = new HashSet<>();
        AtomicBoolean rest = new AtomicBoolean();

        do {
            patterns.add(parsePattern(usedIdentifiers, rest));
        } while (match(TokenType.COMMA));

        return new PatternCase(patterns);
    }

    private Pattern parsePattern(Set<String> usedIdentifiers, AtomicBoolean rest) {
        if (nextIs(TokenType.IDENTIFIER)) {
            Token token = eat(TokenType.IDENTIFIER);
            String name = token.getString();

            if (usedIdentifiers.contains(name)) {
                throw new SyntaxException("Duplicate argument name", token.getPosition());
            } else {
                usedIdentifiers.add(name);
            }

            VariablePattern pattern = new VariablePattern(name);

            if (match(TokenType.ASSIGN)) {
                return new DefaultPattern(pattern, parseExpr(Precedence.FREEFORM_STRUCT));
            }

            return pattern;
        } else if (match(TokenType.REST)) {
            if (rest.get()) {
                throw new SyntaxException("Only one rest argument allowed");
            }

            String name = eat(TokenType.IDENTIFIER).getString();

            if (usedIdentifiers.contains(name)) {
                throw new SyntaxException("Duplicate argument name", getLast().getPosition());
            } else {
                usedIdentifiers.add(name);
            }

            rest.set(true);

            return new RestPattern(name);
        } else if (match(TokenType.LEFT_PAREN)) {
            if (match(TokenType.RIGHT_PAREN)) {
                return new NestedPattern(PatternCase.EMPTY);
            }

            PatternCase patternCase = parsePatternCase();

            eat(TokenType.RIGHT_PAREN);

            return new NestedPattern(patternCase);
        } else if (match(TokenType.UNDERSCORE)) {
            return WildcardPattern.INSTANCE;
        } else {
            match(TokenType.EQUALS);
            return new ValuePattern(parseExpr(Precedence.FREEFORM_STRUCT));
        }
    }
}
