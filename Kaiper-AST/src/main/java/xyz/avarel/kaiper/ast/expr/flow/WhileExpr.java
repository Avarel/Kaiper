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

package xyz.avarel.kaiper.ast.expr.flow;

import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.expr.Expr;
import xyz.avarel.kaiper.lexer.Position;

public class WhileExpr extends Expr {
    private final Expr condition;
    private final Expr action;

    public WhileExpr(Position position, Expr condition, Expr action) {
        super(position);
        this.condition = condition;
        this.action = action;
    }

    public Expr getCondition() {
        return condition;
    }

    public Expr getAction() {
        return action;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append("while");

        builder.append('\n');
        condition.ast("condition", builder, indent + (isTail ? "    " : "│   "), false);

        builder.append('\n');
        action.ast("action", builder, indent + (isTail ? "    " : "│   "), true);
    }

    @Override
    public String toString() {
        return "while";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WhileExpr)) return false;

        WhileExpr whileExpr = (WhileExpr) o;

        return condition.equals(whileExpr.condition) && action.equals(whileExpr.action);
    }

}
