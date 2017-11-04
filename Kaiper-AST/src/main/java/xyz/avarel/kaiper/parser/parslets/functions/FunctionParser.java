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

import xyz.avarel.kaiper.ast.expr.Expr;
import xyz.avarel.kaiper.ast.expr.functions.FunctionNode;
import xyz.avarel.kaiper.ast.expr.variables.Identifier;
import xyz.avarel.kaiper.ast.pattern.Pattern;
import xyz.avarel.kaiper.ast.pattern.PatternCase;
import xyz.avarel.kaiper.ast.pattern.VariablePattern;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.ExprParser;
import xyz.avarel.kaiper.parser.InfixParser;
import xyz.avarel.kaiper.parser.PatternParser;
import xyz.avarel.kaiper.parser.PrefixParser;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class FunctionParser implements PrefixParser {
    @Override
    public Expr parse(ExprParser parser, Token token) {
        if (parser.match(TokenType.UNDERSCORE)) {
            return parseUnderscore(parser, parser.getLast());
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

    private Expr parseUnderscore(ExprParser parser, Token token) {
        ParserProxy ip = new ParserProxy(parser, token);

        Expr expr = ip.parseInfix(0, new Identifier(token.getPosition(), token.getString()));

        List<Pattern> list = new ArrayList<>();

        for (String param : ip.parameters) {
            list.add(new VariablePattern(param));
        }

        return new FunctionNode(token.getPosition(), new PatternCase(list), expr);
    }

    private static final class ParserProxy extends ExprParser {
        private final Set<String> parameters = new LinkedHashSet<>();
        private final ExprParser proxy;
        private ExprParser current;

        private ParserProxy(ExprParser proxy, Token token) {
            super(proxy);

            this.proxy = proxy;
            this.current = this;
            this.parameters.add(token.getString());
        }

        @Override
        public Token eat() {
            Token token = super.eat();
            if (token.getType() == TokenType.UNDERSCORE) {
                parameters.add(token.getString());
                return new Token(token.getPosition(), TokenType.IDENTIFIER, token.getString());
            }
            return token;
        }

        @Override
        public Expr parseInfix(int precedence, Expr left) {
            while (precedence < getPrecedence()) {
                Token token = eat();

                if (token.getType() == TokenType.PIPE_FORWARD) {
                    current = proxy;
                }

                InfixParser infix = getInfixParsers().get(token.getType());

                if (infix == null) throw new SyntaxException("Could not parse token `" + token.getString() + "`", token.getPosition());

                left = infix.parse(current, left, token);
            }
            return left;
        }
    }
}
