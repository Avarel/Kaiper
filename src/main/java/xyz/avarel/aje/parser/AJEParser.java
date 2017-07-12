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

package xyz.avarel.aje.parser;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.value.UndefinedNode;
import xyz.avarel.aje.exceptions.SyntaxException;
import xyz.avarel.aje.lexer.AJELexer;
import xyz.avarel.aje.lexer.Token;
import xyz.avarel.aje.lexer.TokenType;

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

        while (match(TokenType.LINE) || match(TokenType.SEMICOLON)) {
            if (match(TokenType.EOF)) break;

            any = any.andThen(parseExpr());
        }

        return any;
    }

    public Expr parseExpr() {
        return parseExpr(0);
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
}
