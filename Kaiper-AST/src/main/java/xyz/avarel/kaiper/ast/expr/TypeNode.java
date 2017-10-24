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
import xyz.avarel.kaiper.ast.pattern.PatternCase;
import xyz.avarel.kaiper.lexer.Position;

public class TypeNode extends Expr {
    private final String name;
    private final PatternCase patternCase;

    public TypeNode(Position position, String name, PatternCase patternCase) {
        super(position);
        this.name = name;
        this.patternCase = patternCase;
    }

    public String getName() {
        return name;
    }

    public PatternCase getPatternCase() {
        return patternCase;
    }

    @Deprecated
    // IT IS NULL
    public Expr getExpr() {
        return null;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ")
                .append("type").append(name != null ? " " + name : "")
                .append('(').append(patternCase).append(')');
        builder.append('\n');
    }
}
