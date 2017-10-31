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

package xyz.avarel.kaiper.ast.expr.variables;

import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.expr.Expr;
import xyz.avarel.kaiper.lexer.Position;

public class AssignmentExpr extends Expr {
    private final Expr parent;
    private final String name;
    private final Expr expr;

    public AssignmentExpr(Position position, String name, Expr expr) {
        this(position, null, name, expr);
    }

    public AssignmentExpr(Position position, Expr parent, String name, Expr expr) {
        super(position);
        this.parent = parent;
        this.name = name;
        this.expr = expr;
    }

    public AssignmentExpr(Position position, Identifier identifier, Expr expr) {
        this(position, identifier.getParent(), identifier.getName(), expr);
    }

    public Expr getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append("assign");

        if (parent != null) {
            builder.append('\n');
            parent.ast("parent", builder, indent + (isTail ? "    " : "│   "), false);
        }

        builder.append('\n');
        builder.append(indent).append(isTail ? "    " : "│   ").append("├── name: ").append(name);

        builder.append('\n');
        expr.ast(builder, indent + (isTail ? "    " : "│   "), true);
    }

}
