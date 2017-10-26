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

package xyz.avarel.kaiper.ast.expr.collections;

import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.expr.Expr;
import xyz.avarel.kaiper.lexer.Position;

public class SetOperation extends GetOperation {
    private final Expr expr;

    public SetOperation(Position position, Expr left, Expr key, Expr expr) {
        super(position, left, key);
        this.expr = expr;
    }

    public SetOperation(Position position, GetOperation getOp, Expr expr) {
        this(position, getOp.getLeft(), getOp.getKey(), expr);
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
        builder.append(indent).append(isTail ? "└── " : "├── ").append("get");

        builder.append('\n');
        getLeft().ast("target", builder, indent + (isTail ? "    " : "│   "), false);

        builder.append('\n');
        getKey().ast("key", builder, indent + (isTail ? "    " : "│   "), false);

        builder.append('\n');
        expr.ast(builder, indent + (isTail ? "    " : "│   "), true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SetOperation)) return false;
        if (!super.equals(o)) return false;

        SetOperation that = (SetOperation) o;

        return expr.equals(that.expr);
    }

    @Override
    public String toString() {
        return super.toString() + " = " + expr;
    }
}
