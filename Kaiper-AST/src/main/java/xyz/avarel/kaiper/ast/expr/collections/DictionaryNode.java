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

import java.util.Map;

public class DictionaryNode extends Expr {
    private final Map<Expr, Expr> map;

    public DictionaryNode(Position position, Map<Expr, Expr> map) {
        super(position);
        this.map = map;
    }

    public Map<Expr, Expr> getMap() {
        return map;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append("dictionary");
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DictionaryNode && map.equals(((DictionaryNode) obj).map);
    }

    @Override
    public String toString() {
        return map.toString();
    }
}