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
import xyz.avarel.aje.parser.lexer.Position;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

public class AssignmentExpr extends Expr {
    private final boolean declare;
    private final Expr from;
    private final String name;
    private final Expr expr;

    public AssignmentExpr(Position position, Expr from, String name, Expr expr, boolean declaration) {
        super(position);
        this.declare = declaration;
        this.from = from;
        this.name = name;
        this.expr = expr;
    }

    public Expr getFrom() {
        return from;
    }

    public String getName() {
        return name;
    }

    public Expr getExpr() {
        return expr;
    }

    public boolean isDeclaration() {
        return declare;
    }

    @Override
    public Obj accept(ExprVisitor visitor, Scope scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append(declare ? "declare" : "assign");

        builder.append('\n');
        builder.append(indent).append(isTail ? "    " : "│   ").append("├── name: ").append(name);

        if (from != null) {
            builder.append('\n');
            from.ast("of", builder, indent + (isTail ? "    " : "│   "), false);
        }

        builder.append('\n');
        expr.ast(builder, indent + (isTail ? "    " : "│   "), true);
    }

    @Override
    public String toString() {
        return "assign";
    }
}
