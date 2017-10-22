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

package xyz.avarel.kaiper.ast.functions;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.pattern.PatternCase;
import xyz.avarel.kaiper.lexer.Position;

public class FunctionNode extends Expr {
    private final String name;
    private final PatternCase patternCase;
    private final Expr expr;

    public FunctionNode(Position position, PatternCase patternCase, Expr expr) {
        this(position, null, patternCase, expr);
    }

    public FunctionNode(Position position, String name, PatternCase patternCase, Expr expr) {
        super(position);
        this.name = name;
        this.patternCase = patternCase;
        this.expr = expr;
    }

    public String getName() {
        return name;
    }

    public PatternCase getPatternCase() {
        return patternCase;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ")
                .append("def").append(name != null ? " " + name : "")
                .append('(')
                .append(patternCase)
                .append(')');

        builder.append('\n');
        expr.ast(builder, indent + (isTail ? "    " : "│   "), true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FunctionNode)) return false;

        FunctionNode that = (FunctionNode) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (!patternCase.equals(that.patternCase)) return false;
        return expr.equals(that.expr);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + patternCase.hashCode();
        result = 31 * result + expr.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "def" + (name != null ? " " + name : "") + patternCase;
    }
}
