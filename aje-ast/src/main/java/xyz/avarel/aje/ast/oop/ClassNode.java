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

package xyz.avarel.aje.ast.oop;

import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.ast.Single;
import xyz.avarel.aje.ast.functions.FunctionNode;
import xyz.avarel.aje.ast.variables.Identifier;

import java.util.List;
import java.util.Map;

public class ClassNode implements Single {
    private final String name;
    private final Identifier parent;
    private final ConstructorNode constructorNode;
    private final Map<String, Byte> variableDeclarations;
    private final List<FunctionNode> functions;

    public ClassNode(String name, Identifier parent, ConstructorNode constructorNode, Map<String, Byte> variableDeclarations, List<FunctionNode> functions) {
        this.name = name;
        this.parent = parent;
        this.constructorNode = constructorNode;
        this.variableDeclarations = variableDeclarations;
        this.functions = functions;
    }

    public String getName() {
        return name;
    }

    public Identifier getParent() {
        return parent;
    }

    public ConstructorNode getConstructorNode() {
        return constructorNode;
    }

    public Map<String, Byte> getVariableDeclarations() {
        return variableDeclarations;
    }

    public List<FunctionNode> getFunctions() {
        return functions;
    }

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
        return visitor.visit(this, scope);
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ")
                .append("class").append(name != null ? " " + name : "");

        builder.append('\n');

        constructorNode.ast(builder, indent + (isTail ? "    " : "│   "), true);
    }
}