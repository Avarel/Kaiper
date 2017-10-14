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
import xyz.avarel.kaiper.ast.operations.BinaryOperation;
import xyz.avarel.kaiper.ast.value.BooleanNode;
import xyz.avarel.kaiper.ast.value.DecimalNode;
import xyz.avarel.kaiper.ast.value.IntNode;
import xyz.avarel.kaiper.exceptions.SyntaxException;
import xyz.avarel.kaiper.lexer.Position;
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
    public Expr parse(KaiperParser parser, Expr left, Token token) {
        Expr right = parser.parseExpr(getPrecedence() - (isLeftAssoc() ? 0 : 1));

        if (left instanceof IntNode) {
            if (right instanceof IntNode) {
                return opInt(token.getPosition(), ((IntNode) left).getValue(), ((IntNode) right).getValue());
            } else if (right instanceof DecimalNode) {
                return opDecimal(token.getPosition(), ((IntNode) left).getValue(), ((DecimalNode) right).getValue());
            }
        } else if (left instanceof DecimalNode) {
            if (right instanceof IntNode) {
                return opDecimal(token.getPosition(), ((DecimalNode) left).getValue(), ((IntNode) right).getValue());
            } else if (right instanceof DecimalNode) {
                return opDecimal(token.getPosition(), ((DecimalNode) left).getValue(), ((DecimalNode) right).getValue());
            }
        }

        return new BinaryOperation(token.getPosition(), left, right, operator);
    }

    private Expr opInt(Position position, int a, int b) {
        switch (operator) {
            case PLUS: return new IntNode(a + b);
            case MINUS: return new IntNode(a - b);
            case TIMES: return new IntNode(a * b);
            case DIVIDE:
                if (b == 0) throw new SyntaxException("Division by 0", position);
                return new IntNode(a / b);
            case MODULUS: return new IntNode(a % b);
            case POWER: return new IntNode((int) Math.pow(a, b));
            case EQUALS: return BooleanNode.of(a == b);
            case NOT_EQUALS: return BooleanNode.of(a != b);
            case GREATER_THAN: return BooleanNode.of(a > b);
            case GREATER_THAN_EQUAL: return BooleanNode.of(a >= b);
            case LESS_THAN: return BooleanNode.of(a < b);
            case LESS_THAN_EQUAL: return BooleanNode.of(a <= b);
            case SHL: return new IntNode(a << b);
            case SHR: return new IntNode(a >> b);
        }

        return new BinaryOperation(position, new IntNode(a), new IntNode(b), operator);
    }

    private Expr opDecimal(Position position, double a, double b) {
        switch (operator) {
            case PLUS: return new DecimalNode(a + b);
            case MINUS: return new DecimalNode(a - b);
            case TIMES: return new DecimalNode(a * b);
            case DIVIDE: return new DecimalNode(a / b);
            case MODULUS: return new DecimalNode(a % b);
            case POWER: return new DecimalNode((int) Math.pow(a, b));
            case EQUALS: return BooleanNode.of(a == b);
            case NOT_EQUALS: return BooleanNode.of(a != b);
            case GREATER_THAN: return BooleanNode.of(a > b);
            case GREATER_THAN_EQUAL: return BooleanNode.of(a >= b);
            case LESS_THAN: return BooleanNode.of(a < b);
            case LESS_THAN_EQUAL: return BooleanNode.of(a <= b);
        }

        return new BinaryOperation(position, new DecimalNode(a), new DecimalNode(b), operator);
    }
}