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

package xyz.avarel.aje.ast.collections;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.parser.lexer.Position;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

import java.util.List;

public class VectorNode extends Expr {
    private final List<Expr> items;

    public VectorNode(Position position, List<Expr> items) {
        super(position);
        this.items = items;
    }

    public List<Expr> getItems() {
        return items;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append("list");
        for (int i = 0; i < items.size() - 1; i++) {
            builder.append('\n');
            items.get(i).ast(builder, indent + (isTail ? "    " : "│   "), false);
        }
        if (items.size() > 0) {
            builder.append('\n');
            items.get(items.size() - 1).ast(builder, indent + (isTail ? "    " : "│   "), true);
        }
    }

    @Override
    public String toString() {
        return "vector";
    }
}
