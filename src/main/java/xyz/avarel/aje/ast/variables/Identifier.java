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

package xyz.avarel.aje.ast.variables;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

public class Identifier implements Expr {
    private final Expr parent;
    private final String name;

    public Identifier(String name) {
        this(null, name);
    }

    public Identifier(Expr parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public Expr getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
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
            Expr.super.ast(builder, indent, isTail);
        }
    }
}
