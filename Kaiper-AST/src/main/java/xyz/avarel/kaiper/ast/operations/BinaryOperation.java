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

package xyz.avarel.kaiper.ast.operations;

import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.operations.BinaryOperatorType;

public class BinaryOperation implements Single {
    private final Single left;
    private final Single right;
    private final BinaryOperatorType operator;

    public BinaryOperation(Single left, Single right, BinaryOperatorType operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public Single getLeft() {
        return left;
    }

    public Single getRight() {
        return right;
    }

    public BinaryOperatorType getOperator() {
        return operator;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append("binary ").append(operator);

        builder.append('\n');
        left.ast(builder, indent + (isTail ? "    " : "│   "), false);

        builder.append('\n');
        right.ast(builder, indent + (isTail ? "    " : "│   "), true);
    }
}
