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

package xyz.avarel.kaiper.ast.expr.operations;

import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.expr.Expr;
import xyz.avarel.kaiper.lexer.Position;
import xyz.avarel.kaiper.operations.BinaryOperatorType;

public class BinaryOperation extends Expr {
    private final Expr left;
    private final Expr right;
    private final BinaryOperatorType operator;

    public BinaryOperation(Position position, Expr left, Expr right, BinaryOperatorType operator) {
        super(position);
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public Expr getLeft() {
        return left;
    }

    public Expr getRight() {
        return right;
    }

    public BinaryOperatorType getOperator() {
        return operator;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append("binary ").append(operator);

        builder.append('\n');
        left.ast(builder, indent + (isTail ? "    " : "│   "), false);

        builder.append('\n');
        right.ast(builder, indent + (isTail ? "    " : "│   "), true);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BinaryOperation
                && operator.equals(((BinaryOperation) o).operator)
                && left.equals(((BinaryOperation) o).left)
                && right.equals(((BinaryOperation) o).right);
    }

    @Override
    public String toString() {
        return String.valueOf(left) + operator + right;
    }
}
