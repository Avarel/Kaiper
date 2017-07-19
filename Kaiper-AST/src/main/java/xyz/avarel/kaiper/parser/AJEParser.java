/*
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package xyz.avarel.kaiper.parser;

import xyz.avarel.kaiper.Precedence;
import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.ast.value.UndefinedNode;
import xyz.avarel.kaiper.ast.variables.Identifier;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.AJELexer;
import xyz.avarel.kaiper.lexer.Position;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;

import java.util.Objects;

public class AJEParser extends Parser {
    private ParserFlags parserFlags = ParserFlags.ALL_FLAGS;

    public AJEParser(AJELexer tokens) {
        super(tokens, DefaultGrammar.INSTANCE);
    }

    public AJEParser(AJEParser proxy) {
        super(proxy);
    }

    public ParserFlags getParserFlags() {
        return parserFlags;
    }

    public void setParserFlags(ParserFlags parserFlags) {
        this.parserFlags = parserFlags;
    }

    public Expr compile() {
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
        if (match(TokenType.EOF)) return UndefinedNode.VALUE;

        Expr any = parseExpr();

        while (match(TokenType.LINE)) {
            if (match(TokenType.EOF)) break;

            any = any.andThen(parseExpr());
        }

        return any;
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
            throw new SyntaxException("Expected IDENTIFIER but got " + getLast().getType(), position);
        }
    }

    public Single parseSingle() {
        return parseSingle(0);
    }

    public Single parseSingle(int precedence) {
        Expr expr = parseExpr(precedence);
        if (expr instanceof Single) {
            return (Single) expr;
        } else {
            throw new SyntaxException("Internal compiler error");
        }
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
        if (!Objects.equals(eat().getString(), keyword)) {
            throw new SyntaxException("Expected " + keyword.toUpperCase() + " but found " + getLast().getType(),
                    getLast().getPosition());
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
