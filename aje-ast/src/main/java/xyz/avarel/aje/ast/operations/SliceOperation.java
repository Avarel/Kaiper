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

package xyz.avarel.aje.ast.operations;

import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.ast.Single;

public class SliceOperation implements Single {
    private final Single left;
    private final Single start;
    private final Single end;
    private final Single step;

    public SliceOperation(Single left, Single start, Single end, Single step) {
        this.left = left;
        this.start = start;
        this.end = end;
        this.step = step;
    }

    public Single getLeft() {
        return left;
    }

    public Single getStart() {
        return start;
    }

    public Single getEnd() {
        return end;
    }

    public Single getStep() {
        return step;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append("slice");

        builder.append('\n');
        left.ast(builder, indent + (isTail ? "    " : "│   "), false);

        if (start != null) {
            builder.append('\n');
            start.ast("start", builder, indent + (isTail ? "    " : "│   "), false);
        }

        if (end != null) {
            builder.append('\n');
            end.ast("end", builder, indent + (isTail ? "    " : "│   "), false);
        }

        if (step != null) {
            builder.append('\n');
            step.ast("step", builder, indent + (isTail ? "    " : "│   "), true);
        }
    }
}
