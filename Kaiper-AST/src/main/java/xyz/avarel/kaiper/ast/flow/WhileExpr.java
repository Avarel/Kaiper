/*
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package xyz.avarel.kaiper.ast.flow;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.lexer.Position;

public class WhileExpr extends Single {
    private final Single condition;
    private final Expr action;

    public WhileExpr(Position position, Single condition, Expr action) {
        super(position);
        this.condition = condition;
        this.action = action;
    }

    public Single getCondition() {
        return condition;
    }

    public Expr getAction() {
        return action;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
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
}
