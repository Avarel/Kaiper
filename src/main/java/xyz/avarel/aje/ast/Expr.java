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

package xyz.avarel.aje.ast;

import xyz.avarel.aje.ast.flow.ReturnException;
import xyz.avarel.aje.ast.flow.Statements;
import bytecode.AJEBytecode;
import bytecode.serialization.DataOutputConsumer;
import bytecode.serialization.ExprSerializer;
import xyz.avarel.aje.exceptions.AJEException;
import xyz.avarel.aje.exceptions.ComputeException;
import xyz.avarel.aje.interpreter.ExprInterpreter;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.DefaultScope;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Expr {
    <R, C> R accept(ExprVisitor<R, C> visitor, C scope);

    default Expr andThen(Expr after) {
        return new Statements(this, after);
    }

    default void ast(StringBuilder builder, String indent, boolean isTail) {
        builder.append(indent).append(isTail ? "└── " : "├── ").append(toString());
    }

    default void ast(String label, StringBuilder builder, String indent, boolean tail) {
        builder.append(indent).append(tail ? "└── " : "├── ").append(label).append(':');

        builder.append('\n');
        ast(builder, indent + (tail ? "    " : "│   "), true);
    }

    default Obj compute() { //TODO Move this to :interpreter package, somehow...
        try {
            return accept(new ExprInterpreter(), DefaultScope.INSTANCE.copy());
        } catch (ReturnException re) {
            return re.getValue();
        } catch (AJEException re) {
            throw re;
        } catch (RuntimeException re) {
            throw new ComputeException(re);
        }
    }
}
