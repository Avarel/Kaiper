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

package xyz.avarel.kaiper.parser.parslets.variables;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.pattern.Pattern;
import xyz.avarel.kaiper.ast.pattern.PatternCase;
import xyz.avarel.kaiper.ast.pattern.VariablePattern;
import xyz.avarel.kaiper.ast.value.NullNode;
import xyz.avarel.kaiper.ast.variables.BindDeclarationExpr;
import xyz.avarel.kaiper.ast.variables.DeclarationExpr;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.KaiperParser;
import xyz.avarel.kaiper.parser.PatternParser;
import xyz.avarel.kaiper.parser.PrefixParser;

import java.util.ArrayList;
import java.util.List;

public class DeclarationParser implements PrefixParser {
    @Override
    public Expr parse(KaiperParser parser, Token token) {
        if (!parser.getParserFlags().allowVariables()) {
            throw new SyntaxException("Variables are disabled");
        }

        if (parser.match(TokenType.LEFT_PAREN)) {
            PatternCase patternCase = new PatternParser(parser).parsePatternCase();

            parser.match(TokenType.RIGHT_PAREN);

            parser.eat(TokenType.ASSIGN);

            return new BindDeclarationExpr(token.getPosition(), patternCase, parser.parseExpr());
        }

        Token name = parser.eat(TokenType.IDENTIFIER);

        if (parser.match(TokenType.COMMA)) {
            List<Pattern> patterns = new ArrayList<>();

            patterns.add(new VariablePattern(name.getString()));

            do {
                patterns.add(new VariablePattern(parser.eat(TokenType.IDENTIFIER).getString()));
            } while (parser.match(TokenType.COMMA));

            PatternCase patternCase = new PatternCase(patterns);

            parser.eat(TokenType.ASSIGN);

            return new BindDeclarationExpr(
                    token.getPosition(),
                    patternCase,
                    parser.parseExpr()
            );
        }

        if (parser.match(TokenType.ASSIGN)) {
            return new DeclarationExpr(token.getPosition(), name.getString(), parser.parseExpr());
        }

        return new DeclarationExpr(token.getPosition(), name.getString(), NullNode.VALUE);
    }
}
