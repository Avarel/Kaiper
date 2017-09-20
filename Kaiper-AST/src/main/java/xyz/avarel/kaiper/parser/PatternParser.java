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

import xyz.avarel.kaiper.ast.pattern.*;
import xyz.avarel.kaiper.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class PatternParser extends KaiperParser {
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

        String name = eat(TokenType.IDENTIFIER).getString();

        if (match(TokenType.COLON)) {
            // name: EXPR
            basePattern = new TuplePattern(name, parseSingle());
        } else {
            basePattern = new VariablePattern(name);

            if (match(TokenType.ASSIGN)) {
                // name = EXPR
                basePattern = new DefaultPattern(basePattern, parseSingle());
            }
        }

        return basePattern;
    }
}
