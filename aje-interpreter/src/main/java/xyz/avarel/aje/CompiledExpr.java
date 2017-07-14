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

package xyz.avarel.aje;

import xyz.avarel.aje.ast.Expr;
import xyz.avarel.aje.ast.ExprVisitor;
import xyz.avarel.aje.ast.flow.Statements;
import xyz.avarel.aje.exceptions.AJEException;
import xyz.avarel.aje.exceptions.ComputeException;
import xyz.avarel.aje.interpreter.ExprInterpreter;
import xyz.avarel.aje.runtime.Obj;
import xyz.avarel.aje.scope.Scope;

public class CompiledExpr implements Expr {
    private final Expr expr;
    private Scope scope;

    public CompiledExpr(Scope scope, Expr expr) {
        this.scope = scope;
        this.expr = expr;
    }

    @Override
    public Expr andThen(Expr after) {
        if (expr instanceof Statements) {
            ((Statements) expr).getExprs().add(after);
            return this;
        }
        return new CompiledExpr(scope, new Statements(expr, after));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
        try {
            return accept(visitor, scope);
        } catch (ReturnException re) {
            return (R) re.getValue();
        } catch (AJEException re) {
            throw re;
        } catch (RuntimeException re) {
            throw new ComputeException(re);
        }
    }

    public Obj compute() {
        return accept(new ExprInterpreter(), scope.copy());
    }

    @Override
    public void ast(StringBuilder builder, String indent, boolean isTail) {
        expr.ast(builder, indent, isTail);
    }
}
