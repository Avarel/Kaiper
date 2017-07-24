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

package xyz.avarel.kaiper.ast.functions;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.lexer.Position;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionNode extends Single {
    private final String name;
    private final List<ParameterData> parameters;
    private final Expr expr;

    public FunctionNode(Position position, List<ParameterData> parameters, Expr expr) {
        this(position, null, parameters, expr);
    }

    public FunctionNode(Position position, String name, List<ParameterData> parameters, Expr expr) {
        super(position);
        this.name = name;
        this.parameters = parameters;
        this.expr = expr;
    }

    public String getName() {
        return name;
    }

    public List<ParameterData> getParameterExprs() {
        return parameters;
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
                .append("func").append(name != null ? " " + name : "")
                .append('(')
                .append(getParameterExprs().stream().map(Object::toString)
                        .collect(Collectors.joining(", ")))
                .append(')');

        builder.append('\n');
        expr.ast(builder, indent + (isTail ? "    " : "│   "), true);
    }
}
