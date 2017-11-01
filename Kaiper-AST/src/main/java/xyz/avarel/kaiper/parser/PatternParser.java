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

public class PatternParser extends ExprParser {
    public PatternParser(ExprParser parser) {
        super(parser);
    }

    public PatternCase parsePatternCase() {
        return parsePatternCase(new ArrayList<>(), new ParseContext());
    }

    public PatternCase parsePatternCase(List<Pattern> patterns, ParseContext context) {
        do {
            patterns.add(parsePattern(context));
        } while (match(TokenType.COMMA));

        return new PatternCase(patterns);
    }

    private Pattern parsePattern(ParseContext context) {
        if (nextIs(TokenType.IDENTIFIER)) {
            Token token = eat();
            String name = token.getString();

            if (context.usedIdentifiers.contains(name)) {
                throw new SyntaxException("Duplicate argument name", token.getPosition());
            } else {
                context.usedIdentifiers.add(name);
            }

            VariablePattern pattern = new VariablePattern(name);

            if (match(TokenType.ASSIGN)) {
                return new DefaultPattern(pattern, parseExpr(Precedence.FREEFORM_STRUCT));
            }

            return pattern;
        } else if (match(TokenType.REST)) {
            if (context.restPattern) {
                throw new SyntaxException("Only one rest argument allowed");
            }

            Token token = eat();
            String name = token.getString();

            if (context.usedIdentifiers.contains(name)) {
                throw new SyntaxException("Duplicate argument name", token.getPosition());
            } else {
                context.usedIdentifiers.add(name);
            }

            context.restPattern = true;

            return new RestPattern(name);
        } else if (match(TokenType.LEFT_PAREN)) {
            if (match(TokenType.RIGHT_PAREN)) {
                return new TuplePattern(PatternCase.EMPTY);
            }

            PatternCase patternCase = parsePatternCase(new ArrayList<>(), context);

            eat(TokenType.RIGHT_PAREN);

            return new TuplePattern(patternCase);
        } else if (match(TokenType.UNDERSCORE)) {
            return WildcardPattern.INSTANCE;
        } else {
            match(TokenType.EQUALS);
            return new ValuePattern(parseExpr(Precedence.FREEFORM_STRUCT));
        }
    }

    private static class ParseContext {
        final Set<String> usedIdentifiers = new HashSet<>();
        boolean restPattern = false;
    }
}