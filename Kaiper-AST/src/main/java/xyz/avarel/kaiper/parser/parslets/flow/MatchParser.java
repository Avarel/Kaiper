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

package xyz.avarel.kaiper.parser.parslets.flow;

import xyz.avarel.kaiper.ast.expr.Expr;
import xyz.avarel.kaiper.ast.expr.tuples.MatchExpr;
import xyz.avarel.kaiper.ast.expr.tuples.TupleExpr;
import xyz.avarel.kaiper.ast.pattern.PatternCase;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.ExprParser;
import xyz.avarel.kaiper.parser.PatternParser;
import xyz.avarel.kaiper.parser.PrefixParser;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class MatchParser implements PrefixParser {
    @Override
    public Expr parse(ExprParser parser, Token token) {
        Expr expr = parser.parseExpr();

        TupleExpr tuple = new TupleExpr(expr.getPosition(), Collections.singletonList(expr));

        Map<PatternCase, Expr> cases = new TreeMap<>();

        parser.eat(TokenType.LEFT_BRACE);

        PatternParser patternParser = new PatternParser(parser);
        do {
            PatternCase casePattern = patternParser.parsePatternCase();

            if (casePattern.size() > 1) {
                throw new SyntaxException("Complex patterns must be inside parentheses");
            }

            Token arrow = parser.eat(TokenType.ARROW);

            Expr caseExpr;
            if (parser.nextIs(TokenType.LEFT_BRACE)) {
                caseExpr = parser.parseBlock();
            } else {
                caseExpr = parser.parseExpr();
            }

            if (cases.put(casePattern, caseExpr) != null) {
                throw new SyntaxException("Ambiguous match case " + casePattern, arrow.getPosition());
            }
        } while (parser.match(TokenType.LINE));

        parser.eat(TokenType.RIGHT_BRACE);

        return new MatchExpr(token.getPosition(), tuple, cases);
    }
}
