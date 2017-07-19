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

package xyz.avarel.kaiper.ast;

import xyz.avarel.kaiper.ast.collections.*;
import xyz.avarel.kaiper.ast.flow.ConditionalExpr;
import xyz.avarel.kaiper.ast.flow.ForEachExpr;
import xyz.avarel.kaiper.ast.flow.ReturnExpr;
import xyz.avarel.kaiper.ast.flow.Statements;
import xyz.avarel.kaiper.ast.functions.FunctionNode;
import xyz.avarel.kaiper.ast.invocation.Invocation;
import xyz.avarel.kaiper.ast.operations.BinaryOperation;
import xyz.avarel.kaiper.ast.operations.SliceOperation;
import xyz.avarel.kaiper.ast.operations.UnaryOperation;
import xyz.avarel.kaiper.ast.value.*;
import xyz.avarel.kaiper.ast.variables.AssignmentExpr;
import xyz.avarel.kaiper.ast.variables.DeclarationExpr;
import xyz.avarel.kaiper.ast.variables.Identifier;

public interface ExprVisitor<R, C> {
    R visit(Statements statements, C scope);

    R visit(FunctionNode expr, C scope);

    R visit(Identifier expr, C scope);

    R visit(Invocation expr, C scope);

    R visit(BinaryOperation expr, C scope);

    R visit(UnaryOperation expr, C scope);

    R visit(RangeNode expr, C scope);

    R visit(ArrayNode expr, C scope);

    R visit(SliceOperation expr, C scope);

    R visit(AssignmentExpr expr, C scope);

    R visit(GetOperation expr, C scope);

    R visit(SetOperation expr, C scope);

    R visit(ReturnExpr expr, C scope);

    R visit(ConditionalExpr expr, C scope);

    R visit(ForEachExpr expr, C scope);

    R visit(DictionaryNode expr, C scope);

    R visit(UndefinedNode undefinedNode, C scope);

    R visit(IntNode intNode, C scope);

    R visit(DecimalNode decimalNode, C scope);

    R visit(BooleanNode booleanNode, C scope);

    R visit(StringNode stringNode, C scope);

    R visit(DeclarationExpr declarationExpr, C scope);

    R visit(ModuleNode moduleNode, C scope);

    R visit(TypeNode typeNode, C scope);
}
