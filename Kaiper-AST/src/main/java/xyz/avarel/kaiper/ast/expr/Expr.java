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

package xyz.avarel.kaiper.ast.expr;

import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.lexer.Position;

/**
 * Base implementation of a node for the visitor pattern.
 */
public abstract class Expr {
    private final Position position;

    protected Expr(Position position) {
        this.position = position;
    }

    public abstract <R, C> R accept(ExprVisitor<R, C> visitor, C scope);

    /**
     * Appends the AST information of the node to the buffer.
     *
     * @param builder Target buffer.
     * @param indent Current indentation level.
     * @param isTail If the node is a tail node.
     */
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append(toString());
    }

    /**
     * Appends the AST information of the node to the buffer.
     *
     * @param label String label.
     * @param builder Target buffer.
     * @param indent Current indentation level.
     * @param tail If the node is a tail node.
     */
    public void ast(String label, StringBuilder builder, String indent, boolean tail) {
        builder.append(indent).append(tail ? "└── " : "├── ").append(label).append(':');

        builder.append('\n');
        ast(builder, indent + (tail ? "    " : "│   "), true);
    }

    /**
     * @return The position of the node.
     * @see Position
     */
    public Position getPosition() {
        return position;
    }
}
