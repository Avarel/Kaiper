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

package xyz.avarel.kaiper.ast.expr.tuples;

import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.expr.Expr;
import xyz.avarel.kaiper.lexer.Position;

import java.util.Iterator;
import java.util.List;

public class TupleExpr extends Expr {
    private final List<Expr> elements;

    public TupleExpr(Position position, List<Expr> elements) {
        super(position);
        this.elements = elements;
    }

    public List<Expr> getElements() {
        return elements;
    }

    public int size() {
        return elements.size();
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TupleExpr && elements.equals(((TupleExpr) o).elements);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");

        Iterator<Expr> iterator = elements.iterator();

        while (true) {
            sb.append(iterator.next());
            if (iterator.hasNext()) {
                sb.append(", ");
            } else {
                break;
            }
        }

        sb.append(')');

        return sb.toString();
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append("list");
        for (int i = 0; i < elements.size() - 1; i++) {
            builder.append('\n');
            elements.get(i).ast(builder, indent + (isTail ? "    " : "│   "), false);
        }
        if (elements.size() > 0) {
            builder.append('\n');
            elements.get(elements.size() - 1).ast(builder, indent + (isTail ? "    " : "│   "), true);
        }
    }
}