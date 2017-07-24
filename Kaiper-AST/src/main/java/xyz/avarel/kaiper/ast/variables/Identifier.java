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

package xyz.avarel.kaiper.ast.variables;

import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.lexer.Position;

public class Identifier extends Single {
    private final Single parent;
    private final String name;

    public Identifier(Position position, String name) {
        this(position, null, name);
    }

    public Identifier(Position position, Single parent, String name) {
        super(position);
        this.parent = parent;
        this.name = name;
    }

    public Single getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public String toString() {

        return name;
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        if (parent != null) {
            builder.append(indent).append(isTail ? "└── " : "├── ").append("attribute");

            builder.append('\n');
            parent.ast("parent", builder, indent + (isTail ? "    " : "│   "), false);

            builder.append('\n');
            builder.append(indent).append(isTail ? "    " : "│   ").append("└── ").append(name);
        } else {
            super.ast(builder, indent, isTail);
        }
    }
}
