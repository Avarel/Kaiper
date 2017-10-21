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

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.flow.ConditionalExpr;
import xyz.avarel.kaiper.ast.value.BooleanNode;
import xyz.avarel.kaiper.ast.value.NullNode;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.parser.ExprParser;
import xyz.avarel.kaiper.parser.PrefixParser;

public class IfElseParser implements PrefixParser {
    @Override
    public Expr parse(ExprParser parser, Token token) {
        Expr condition = parser.parseExpr();

        parser.eat(TokenType.LEFT_BRACE);
        Expr ifBranch = parser.parseStatements();
        parser.eat(TokenType.RIGHT_BRACE);

        Expr elseBranch = null;

        if (parser.matchSignificant(TokenType.ELSE)) {
            if (parser.nextIs(TokenType.IF)) {
                elseBranch = parser.parseExpr();
            } else {
                parser.eat(TokenType.LEFT_BRACE);
                elseBranch = parser.parseStatements();
                parser.eat(TokenType.RIGHT_BRACE);
            }
        }

        if (condition == BooleanNode.TRUE) {
            return ifBranch;
        } else if (condition == BooleanNode.FALSE) {
            return elseBranch != null ? elseBranch : NullNode.VALUE;
        }

        return new ConditionalExpr(token.getPosition(), condition, ifBranch, elseBranch);
    }
}
