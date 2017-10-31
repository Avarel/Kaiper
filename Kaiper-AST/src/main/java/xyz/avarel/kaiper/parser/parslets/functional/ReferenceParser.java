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

package xyz.avarel.kaiper.parser.parslets.functional;

import xyz.avarel.kaiper.Precedence;
import xyz.avarel.kaiper.ast.expr.Expr;
import xyz.avarel.kaiper.ast.expr.invocation.Invocation;
import xyz.avarel.kaiper.ast.expr.operations.BinaryOperation;
import xyz.avarel.kaiper.ast.expr.tuples.TupleExpr;
import xyz.avarel.kaiper.ast.expr.variables.Identifier;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.operations.BinaryOperatorType;
import xyz.avarel.kaiper.parser.BinaryParser;
import xyz.avarel.kaiper.parser.ExprParser;

import java.util.Arrays;
import java.util.Collections;

// def (re, im)::conjugate() = re, -im
// def conjugate(re, im) = re, -im
public class ReferenceParser extends BinaryParser {
    public ReferenceParser() {
        super(Precedence.REF);
    }

    @Override
    public Expr parse(ExprParser parser, Expr left, Token token) {
        Identifier identifier = parser.parseIdentifier();

        if (parser.match(TokenType.LEFT_PAREN)) {
            Expr right;
            if (parser.match(TokenType.RIGHT_PAREN)) {
                right = new TupleExpr(parser.getPosition(), Collections.emptyList());
            } else {
                right = parser.parseExpr();
                parser.eat(TokenType.RIGHT_PAREN);
            }

            TupleExpr tuple = new TupleExpr(left.getPosition(), Arrays.asList(left, right));
            return new Invocation(tuple.getPosition(), identifier, tuple);
        }

        return new BinaryOperation(token.getPosition(), left, identifier, BinaryOperatorType.REF);
    }
}
