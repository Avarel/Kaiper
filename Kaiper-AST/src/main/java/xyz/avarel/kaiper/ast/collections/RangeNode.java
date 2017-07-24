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

package xyz.avarel.kaiper.ast.collections;

import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.lexer.Position;

public class RangeNode extends Single {
    private final Single left;
    private final Single right;

    public RangeNode(Position position, Single left, Single right) {
        super(position);
        this.left = left;
        this.right = right;
    }

    public Single getLeft() {
        return left;
    }

    public Single getRight() {
        return right;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append("range");

        builder.append('\n');
        left.ast("start", builder, indent + (isTail ? "    " : "│   "), false);

        builder.append('\n');
        right.ast("end", builder, indent + (isTail ? "    " : "│   "), true);
    }
}
