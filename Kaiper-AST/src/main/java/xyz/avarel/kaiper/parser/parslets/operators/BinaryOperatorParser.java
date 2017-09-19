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

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.ast.operations.BinaryOperation;
import xyz.avarel.kaiper.ast.value.BooleanNode;
import xyz.avarel.kaiper.ast.value.DecimalNode;
import xyz.avarel.kaiper.ast.value.IntNode;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Token;
import xyz.avarel.kaiper.operations.BinaryOperatorType;
import xyz.avarel.kaiper.parser.BinaryParser;
import xyz.avarel.kaiper.parser.KaiperParser;

public class BinaryOperatorParser extends BinaryParser {
    private final BinaryOperatorType operator;

    public BinaryOperatorParser(int precedence, boolean leftAssoc, BinaryOperatorType operator) {
        super(precedence, leftAssoc);
        this.operator = operator;
    }

    @Override
    public Expr parse(KaiperParser parser, Single left, Token token) {
        Single right = parser.parseSingle(getPrecedence() - (isLeftAssoc() ? 0 : 1));

        if ((left instanceof IntNode || left instanceof DecimalNode)
                && (right instanceof IntNode || right instanceof DecimalNode)) {
            return optimizeArithmetic(parser, token, left, right, operator);
        }

        return new BinaryOperation(token.getPosition(), left, right, operator);
    }

    private Single optimizeArithmetic(KaiperParser parser, Token token, Single left, Single right, BinaryOperatorType operator) {
        boolean endInt = left instanceof IntNode && right instanceof IntNode;
        double leftValue;
        if (left instanceof IntNode) {
            leftValue = ((IntNode) left).getValue();
        } else {
            leftValue = ((DecimalNode) left).getValue();
        }

        double rightValue;
        if (right instanceof IntNode) {
            rightValue = ((IntNode) right).getValue();
        } else {
            rightValue = ((DecimalNode) right).getValue();
        }

        double finalValue;

        switch (operator) {
            case PLUS:
                finalValue = leftValue + rightValue;
                break;
            case MINUS:
                finalValue = leftValue - rightValue;
                break;
            case TIMES:
                finalValue = leftValue * rightValue;
                break;
            case DIVIDE:
                if (endInt && rightValue == 0) {
                    throw new SyntaxException("Division by 0", parser.getLast().getPosition());
                }
                finalValue = leftValue / rightValue;
                break;
            case MODULUS:
                finalValue = leftValue % rightValue;
                break;
            case POWER:
                finalValue = Math.pow(leftValue, rightValue);
                break;

            case EQUALS:
                return BooleanNode.of(leftValue == rightValue);
            case NOT_EQUALS:
                return BooleanNode.of(leftValue != rightValue);
            case GREATER_THAN:
                return BooleanNode.of(leftValue > rightValue);
            case GREATER_THAN_EQUAL:
                return BooleanNode.of(leftValue >= rightValue);
            case LESS_THAN:
                return BooleanNode.of(leftValue < rightValue);
            case LESS_THAN_EQUAL:
                return BooleanNode.of(leftValue <= rightValue);

            default:
                return new BinaryOperation(token.getPosition(), left, right, operator);
        }

        if (endInt) {
            return new IntNode(token.getPosition(), (int) finalValue);
        }

        return new DecimalNode(token.getPosition(), finalValue);
    }
}