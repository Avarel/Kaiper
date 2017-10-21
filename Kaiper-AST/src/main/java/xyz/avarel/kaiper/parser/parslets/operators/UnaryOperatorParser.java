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

package xyz.avarel.kaiper.parser.parslets.operators;

import xyz.avarel.kaiper.Precedence;
import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.operations.UnaryOperation;
import xyz.avarel.kaiper.ast.value.BooleanNode;
import xyz.avarel.kaiper.ast.value.DecimalNode;
import xyz.avarel.kaiper.ast.value.IntNode;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.operations.UnaryOperatorType;
import xyz.avarel.kaiper.parser.ExprParser;
import xyz.avarel.kaiper.parser.PrefixParser;

public class UnaryOperatorParser implements PrefixParser {
    private final UnaryOperatorType operator;

    public UnaryOperatorParser(UnaryOperatorType operator) {
        this.operator = operator;
    }

    @Override
    public Expr parse(ExprParser parser, Token token) {
        Expr left = parser.parseExpr(Precedence.PREFIX);

        if (left instanceof IntNode) {
            if (operator == UnaryOperatorType.MINUS) {
                IntNode target = (IntNode) left;
                return new IntNode(-target.getValue());
            }
        } else if (left instanceof DecimalNode) {
            if (operator == UnaryOperatorType.MINUS) {
                DecimalNode target = (DecimalNode) left;
                return new DecimalNode(-target.getValue());
            }
        } else if (left instanceof BooleanNode) {
            if (operator == UnaryOperatorType.NEGATE) {
                return left == BooleanNode.TRUE ? BooleanNode.FALSE : BooleanNode.TRUE;
            }
        }

        return new UnaryOperation(token.getPosition(), left, operator);
    }
}
