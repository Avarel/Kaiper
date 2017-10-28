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
import xyz.avarel.kaiper.ast.expr.tuples.FreeFormStruct;
import xyz.avarel.kaiper.ast.expr.tuples.TupleExpr;
import xyz.avarel.kaiper.ast.expr.variables.Identifier;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.lexer.TokenType;
import xyz.avarel.kaiper.operations.BinaryOperatorType;
import xyz.avarel.kaiper.parser.BinaryParser;
import xyz.avarel.kaiper.parser.ExprParser;

import java.util.ArrayList;
import java.util.LinkedHashMap;

// def (re, im)::conjugate() = re: self.re, im: -self.im
// def conjugate(self: (re, im)) = re: self.re, im: -self.im
public class ReferenceParser extends BinaryParser {
    public ReferenceParser() {
        super(Precedence.REF);
    }

    @Override
    public Expr parse(ExprParser parser, Expr left, Token token) {
        Identifier identifier = parser.parseIdentifier();

        boolean leftParen = parser.match(TokenType.LEFT_PAREN);
        if (leftParen || parser.nextIsAny(InvocationParser.argumentTokens)) {
            Expr expr;
            if (leftParen) {
                if (parser.match(TokenType.RIGHT_PAREN)) {
                    expr = new FreeFormStruct(parser.getLast().getPosition(), new LinkedHashMap<>());
                } else {
                    expr = parser.parseExpr();
                    parser.eat(TokenType.RIGHT_PAREN);
                }
            } else {
                expr = parser.parseExpr();
            }

            TupleExpr tuple;

            if (expr instanceof TupleExpr) {
                tuple = (TupleExpr) expr;
            } else {
                tuple = new TupleExpr(expr.getPosition(), new ArrayList<>());
            }

            tuple.getElements().add(left);

            return new Invocation(tuple.getPosition(), identifier, tuple);
        }

        return new BinaryOperation(token.getPosition(), left, identifier, BinaryOperatorType.REF);
    }
}
