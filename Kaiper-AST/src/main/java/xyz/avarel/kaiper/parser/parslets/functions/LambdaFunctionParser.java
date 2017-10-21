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

package xyz.avarel.kaiper.parser.parslets.functions;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.functions.FunctionNode;
import xyz.avarel.kaiper.ast.pattern.DefaultPattern;
import xyz.avarel.kaiper.ast.pattern.PatternCase;
import xyz.avarel.kaiper.ast.pattern.VariablePattern;
import xyz.avarel.kaiper.ast.value.NullNode;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.ExprParser;
import xyz.avarel.kaiper.parser.PatternParser;
import xyz.avarel.kaiper.parser.PrefixParser;

public class LambdaFunctionParser implements PrefixParser {
    @Override
    public Expr parse(ExprParser parser, Token token) {
        if (parser.match(TokenType.RIGHT_BRACE)) {
            return new FunctionNode(token.getPosition(), PatternCase.EMPTY, NullNode.VALUE);
        }

        // Check for arrows.
        int peek = 0;
        boolean hasArrow = false;
        lookAhead:
        while (!parser.nextIs(TokenType.RIGHT_BRACE)) {
            TokenType type = parser.peek(peek).getType();
            switch (type) {
                case IDENTIFIER:
                case COLON:
                case COMMA:
                    peek++;
                    break;
                case ARROW:
                    hasArrow = true;
                default:
                    break lookAhead;
            }
        }

        PatternCase patternCase;

        if (hasArrow) {
            if (parser.match(TokenType.ARROW)) {
                patternCase = PatternCase.EMPTY;
            } else {
                patternCase = new PatternParser(parser).parsePatternCase();
                parser.eat(TokenType.ARROW);
            }
        } else {
            patternCase =  new PatternCase(new DefaultPattern(new VariablePattern("it"), NullNode.VALUE));
        }

        Expr expr = parser.parseStatements();

        parser.eat(TokenType.RIGHT_BRACE);

        return new FunctionNode(token.getPosition(), patternCase, expr);
    }
}
