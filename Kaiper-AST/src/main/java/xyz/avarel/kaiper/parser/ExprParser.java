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
import xyz.avarel.kaiper.ast.expr.Expr;
import xyz.avarel.kaiper.ast.expr.flow.Statements;
import xyz.avarel.kaiper.ast.expr.value.NullNode;
import xyz.avarel.kaiper.ast.expr.variables.Identifier;
import xyz.avarel.kaiper.ast.pattern.PatternCase;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.KaiperLexer;
import xyz.avarel.kaiper.lexer.Position;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExprParser extends Parser {
    private PatternParser patternParser = null;

    public ExprParser(KaiperLexer tokens) {
        super(tokens, DefaultGrammar.INSTANCE);
    }

    public ExprParser(ExprParser proxy) {
        super(proxy);
    }

    public PatternCase parsePattern() {
        if (patternParser == null) {
            patternParser = new PatternParser(this);
        }
        return patternParser.parsePatternCase();
    }

    public Expr parse() {
        Expr expr = parseStatements();

        if (!getTokens().isEmpty()) {
            Token t = getTokens().get(0);
            if (t.getType() != TokenType.EOF) {
                throw new SyntaxException("Unexpected " + t, t.getPosition());
            }
        }

        return expr;
    }

    public Expr parseStatements() {
        List<Expr> exprList = new ArrayList<>();

        do {
            if (match(TokenType.EOF)) break;
            exprList.add(parseExpr());
        } while (match(TokenType.LINE));

        return exprList.size() == 1 ? exprList.get(0) : new Statements(exprList);
    }

    public Expr parseExpr() {
        return parseExpr(0);
    }

    public Identifier parseIdentifier() {
        Position position = peek(0).getPosition();
        Expr expr = parseExpr(Precedence.DOT - 1);
        if (expr instanceof Identifier) {
            return (Identifier) expr;
        } else {
            throw new SyntaxException("Expected IDENTIFIER", position);
        }
    }

    public Expr parseBlock() {
        Expr expr = NullNode.VALUE;
        eat(TokenType.LEFT_BRACE);
        if (!match(TokenType.RIGHT_BRACE)) {
            expr = parseStatements();
            eat(TokenType.RIGHT_BRACE);
        }
        return expr;
    }

    public Expr parseExpr(int precedence) {
        Token token = eat();

        Expr expr = parsePrefix(token);

        return parseInfix(precedence, expr);
    }

    public Expr parsePrefix(Token token) {
        PrefixParser prefix = getPrefixParsers().get(token.getType());

        if (prefix == null) throw new SyntaxException("Unexpected " + token, token.getPosition());

        return prefix.parse(this, token);
    }

    public Expr parseInfix(int precedence, Expr left) {
        while (precedence < getPrecedence()) {
            Token token = eat();

            InfixParser infix = getInfixParsers().get(token.getType());

            if (infix == null) {
                throw new SyntaxException("Unexpected " + token, token.getPosition());
            } else {
                left = infix.parse(this, left, token);
            }
        }
        return left;
    }

    public void eatSoftKeyword(String keyword) {
        Token token;
        if (!Objects.equals((token = eat()).getString(), keyword)) {
            throw new SyntaxException("Expected `" + keyword + "`", token.getPosition());
        }
    }

    public boolean matchSoftKeyword(String keyword) {
        if (Objects.equals(peek(0).getString(), keyword)) {
            eat();
            return true;
        }
        return false;
    }
}
