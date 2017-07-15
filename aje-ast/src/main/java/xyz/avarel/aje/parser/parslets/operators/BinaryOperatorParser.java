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

package xyz.avarel.aje.parser.parslets.operators;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.Single;
import xyz.avarel.aje.ast.operations.BinaryOperation;
import xyz.avarel.aje.ast.operations.BinaryOperatorType;
import xyz.avarel.aje.ast.value.BooleanNode;
import xyz.avarel.aje.ast.value.DecimalNode;
import xyz.avarel.aje.ast.value.IntNode;
import xyz.avarel.aje.ast.variables.AssignmentExpr;
import xyz.avarel.aje.ast.variables.Identifier;
import xyz.avarel.aje.exceptions.SyntaxException;
import xyz.avarel.aje.lexer.Token;
import xyz.avarel.aje.lexer.TokenType;
import xyz.avarel.aje.parser.AJEParser;
import xyz.avarel.aje.parser.BinaryParser;

public class BinaryOperatorParser extends BinaryParser {
    private final BinaryOperatorType operator;

    public BinaryOperatorParser(int precedence, boolean leftAssoc, BinaryOperatorType operator) {
        super(precedence, leftAssoc);
        this.operator = operator;
    }

    @Override
    public Expr parse(AJEParser parser, Single left, Token token) {
        if (left instanceof Identifier) {
            if (parser.match(TokenType.ASSIGN)) {
                if (parser.getLast().getPosition().getIndex() - token.getPosition().getIndex() != 2) {
                    throw new SyntaxException("Compound assignment requires assign token directly next to operator",
                            parser.getLast().getPosition());
                }

                Single right = parser.parseSingle();
                return new AssignmentExpr(
                        ((Identifier) left).getName(),
                        new BinaryOperation(left, right, operator)
                );
            }
        }

        Single right = parser.parseSingle(getPrecedence() - (isLeftAssoc() ? 0 : 1));

        if ((left instanceof IntNode || left instanceof DecimalNode)
                && (right instanceof IntNode || right instanceof DecimalNode)) {
            return optimizeArithmetic(parser, left, right, operator);
        }

        return new BinaryOperation(left, right, operator);
    }

    private Single optimizeArithmetic(AJEParser parser, Single left, Single right, BinaryOperatorType operator) {
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
                return new BinaryOperation(left, right, operator);
        }

        if (endInt) {
            return new IntNode((int) finalValue);
        }

        return new DecimalNode(finalValue);
    }
}