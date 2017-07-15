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

import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.ast.Single;

public class DeclarationExpr implements Single {
    private final String name;
    private final Single expr;
    private final byte flags;

    public DeclarationExpr(String name, Single expr) {
        this(name, expr, (byte) 0);
    }

    public DeclarationExpr(String name, Single expr, byte flags) {
        this.name = name;
        this.expr = expr;
        this.flags = flags;
    }

    public String getName() {
        return name;
    }

    public Single getExpr() {
        return expr;
    }

    public byte getFlags() {
        return flags;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append("declare");

        builder.append('\n');
        builder.append(indent).append(isTail ? "    " : "│   ").append("├── name: ").append(name);

        builder.append('\n');
        expr.ast(builder, indent + (isTail ? "    " : "│   "), true);
    }
}
