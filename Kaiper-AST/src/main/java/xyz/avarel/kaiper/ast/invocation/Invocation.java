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

package xyz.avarel.kaiper.ast.invocation;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.tuples.TupleExpr;
import xyz.avarel.kaiper.lexer.Position;

public class Invocation extends Expr {
    private final Expr left;
    private final TupleExpr argument;

    public Invocation(Position position, Expr left, TupleExpr argument) {
        super(position);
        this.left = left;
        this.argument = argument;
    }

    public Expr getLeft() {
        return left;
    }

    public TupleExpr getArgument() {
        return argument;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append("invoke");

        builder.append('\n');
        left.ast("target", builder, indent + (isTail ? "    " : "│   "), false);

        builder.append('\n');
        argument.ast("argument", builder, indent + (isTail ? "    " : "│   "), true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Invocation)) return false;

        Invocation that = (Invocation) o;
        return left.equals(that.left) && argument.equals(that.argument);
    }

    @Override
    public int hashCode() {
        int result = left.hashCode();
        result = 31 * result + argument.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return left + "(" + argument + ")";
    }
}
