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
import xyz.avarel.kaiper.ast.pattern.PatternCase;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.KaiperParser;
import xyz.avarel.kaiper.parser.PatternParser;
import xyz.avarel.kaiper.parser.PrefixParser;

public class FunctionParser implements PrefixParser {
    private LambdaFunctionParser lambda = new LambdaFunctionParser();
    private ImplicitFunctionParser implicit = new ImplicitFunctionParser();

    @Override
    public Expr parse(KaiperParser parser, Token token) {
        if (!parser.getParserFlags().allowFunctionCreation()) {
            throw new SyntaxException("Function creation are disabled");
        }

        if (parser.matchSignificant(TokenType.LEFT_BRACE)) {
            return lambda.parse(parser, parser.getLast());
        } else if (parser.match(TokenType.UNDERSCORE)) {
            return implicit.parse(parser, parser.getLast());
        }

        String name = null;
        if (parser.match(TokenType.IDENTIFIER)) {
            name = parser.getLast().getString();
        }

        PatternCase patternCase;
        parser.eat(TokenType.LEFT_PAREN);
        if (!parser.match(TokenType.RIGHT_PAREN)) {
            patternCase = new PatternParser(parser).parsePatternCase();
            parser.eat(TokenType.RIGHT_PAREN);
        } else {
            patternCase = PatternCase.EMPTY;
        }

        Expr expr;

        if (parser.match(TokenType.ASSIGN)) {
            expr = parser.parseExpr();
        } else {
            expr = parser.parseBlock();
        }

        return new FunctionNode(token.getPosition(), name, patternCase, expr);
    }
}
