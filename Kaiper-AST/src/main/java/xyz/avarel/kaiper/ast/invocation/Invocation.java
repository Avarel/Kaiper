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

package xyz.avarel.kaiper.ast.invocation;

import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.Single;
import xyz.avarel.kaiper.lexer.Position;

import java.util.List;

public class Invocation extends Single {
    private final Single left;
    private final List<Single> arguments;

    public Invocation(Position position, Single left, List<Single> arguments) {
        super(position);
        this.left = left;
        this.arguments = arguments;
    }

    public Single getLeft() {
        return left;
    }

    public List<Single> getArguments() {
        return arguments;
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
        if (arguments.isEmpty()) {
            builder.append(indent).append(isTail ? "    " : "│   ").append("└── ").append("*");
        } else {
            for (int i = 0; i < arguments.size() - 1; i++) {
                arguments.get(i).ast(builder, indent + (isTail ? "    " : "│   "), false);
                builder.append('\n');
            }
            if (arguments.size() > 0) {
                arguments.get(arguments.size() - 1).ast(builder, indent + (isTail ? "    " : "│   "), true);
            }
        }
    }
}
