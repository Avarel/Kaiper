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
import xyz.avarel.kaiper.operations.UnaryOperatorType;

public class UnaryOperation extends Expr {
    private final Expr target;
    private final UnaryOperatorType operator;

    public UnaryOperation(Position position, Expr target, UnaryOperatorType operator) {
        super(position);
        this.target = target;
        this.operator = operator;
    }

    public Expr getTarget() {
        return target;
    }

    public UnaryOperatorType getOperator() {
        return operator;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append("unary ").append(operator);

        builder.append('\n');
        target.ast(builder, indent + (isTail ? "    " : "│   "), true);
    }

}
