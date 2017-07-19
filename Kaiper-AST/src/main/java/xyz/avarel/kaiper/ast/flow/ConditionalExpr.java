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

public class ConditionalExpr implements Single {
    private final Single condition;
    private final Expr ifBranch;
    private final Expr elseBranch;

    public ConditionalExpr(Single condition, Expr ifBranch, Expr elseBranch) {
        this.condition = condition;
        this.ifBranch = ifBranch;
        this.elseBranch = elseBranch;
    }

    public Single getCondition() {
        return condition;
    }

    public Expr getIfBranch() {
        return ifBranch;
    }

    public Expr getElseBranch() {
        return elseBranch;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) { // return if (sin(0) == 0) sin(50) else false
        builder.append(indent).append(isTail ? "└── " : "├── ").append("if");

        builder.append('\n');
        condition.ast("condition", builder, indent + (isTail ? "    " : "│   "), false);

        builder.append('\n');
        ifBranch.ast("true", builder, indent + (isTail ? "    " : "│   "), elseBranch == null);

        if (elseBranch != null) {
            builder.append('\n');
            elseBranch.ast("false", builder, indent + (isTail ? "    " : "│   "), true);
        }
    }
}
