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

package xyz.avarel.kaiper.ast.collections;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.lexer.Position;

import java.util.List;

public class ArrayNode extends Expr {
    private final List<Expr> items;

    public ArrayNode(Position position, List<Expr> items) {
        super(position);
        this.items = items;
    }

    public List<Expr> getItems() {
        return items;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
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
    public boolean equals(Object obj) {
        return obj instanceof ArrayNode && items.equals(((ArrayNode) obj).items);
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
