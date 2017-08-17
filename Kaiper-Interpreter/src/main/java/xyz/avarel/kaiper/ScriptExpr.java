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

package xyz.avarel.kaiper;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.ExprVisitor;
import xyz.avarel.kaiper.ast.flow.Statements;
import xyz.avarel.kaiper.exceptions.ComputeException;
import xyz.avarel.kaiper.exceptions.KaiperException;
import xyz.avarel.kaiper.exceptions.ReturnException;
import xyz.avarel.kaiper.interpreter.ExprInterpreter;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.scope.Scope;

public class ScriptExpr extends Expr {
    private final Expr expr;
    private final Scope scope;

    public ScriptExpr(Scope scope, Expr expr) {
        super(expr.getPosition());
        this.scope = scope;
        this.expr = expr;
    }

    @Override
    public Expr andThen(Expr after) {
        if (expr instanceof Statements) {
            ((Statements) expr).getExprs().add(after);
            return this;
        }
        return new ScriptExpr(scope, new Statements(expr, after));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R, C> R accept(ExprVisitor<R, C> visitor, C scope) {
        try {
            return expr.accept(visitor, scope);
        } catch (ReturnException re) {
            return (R) re.getValue();
        } catch (KaiperException re) {
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
