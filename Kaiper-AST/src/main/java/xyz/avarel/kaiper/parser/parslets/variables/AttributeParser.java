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

import xyz.avarel.kaiper.Precedence;
import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.variables.AssignmentExpr;
import xyz.avarel.kaiper.ast.variables.Identifier;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.BinaryParser;
import xyz.avarel.kaiper.parser.ExprParser;
import xyz.avarel.kaiper.parser.parslets.functional.InvocationParser;

public class AttributeParser extends BinaryParser {
    public AttributeParser() {
        super(Precedence.DOT);
    }

    @Override
    public Expr parse(ExprParser parser, Expr left, Token token) {
        Token name = parser.eat(TokenType.IDENTIFIER);

        if (parser.match(TokenType.ASSIGN)) {
            return new AssignmentExpr(token.getPosition(), left, name.getString(), parser.parseExpr());
        }

        Identifier id = new Identifier(token.getPosition(), left, name.getString());

        if (parser.nextIsAny(InvocationParser.argumentTokens)) {
            return InvocationParser.tupleInvocationCheck(id, parser.parseExpr());
        }

        return id;
    }
}
