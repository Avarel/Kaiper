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

package xyz.avarel.kaiper.runtime.functions;

import xyz.avarel.kaiper.ast.Expr;
import xyz.avarel.kaiper.ast.pattern.Pattern;
import xyz.avarel.kaiper.ast.pattern.PatternCase;
import xyz.avarel.kaiper.ast.pattern.RestPattern;
import xyz.avarel.kaiper.exceptions.InterpreterException;
import xyz.avarel.kaiper.exceptions.ReturnException;
import xyz.avarel.kaiper.interpreter.ExprInterpreter;
import xyz.avarel.kaiper.interpreter.PatternBinder;
import xyz.avarel.kaiper.runtime.Obj;
import xyz.avarel.kaiper.runtime.Tuple;
import xyz.avarel.kaiper.scope.Scope;

import java.util.List;

public class CompiledFunc extends Func {
    private final Expr expr;
    private final Scope scope;
    private final ExprInterpreter visitor;
    private final PatternCase patternCase;

    public CompiledFunc(String name, PatternCase patternCase, Expr expr, ExprInterpreter visitor, Scope scope) {
        super(name);
        this.patternCase = patternCase;
        this.expr = expr;
        this.scope = scope;
        this.visitor = visitor;
    }

    @Override
    public int getArity() {
        boolean rest = false;
        for (Pattern pattern : patternCase.getPatterns()) {
            if (pattern instanceof RestPattern) rest = true;
        }

        return patternCase.size() - (rest ? 1 : 0);
    }

    public List<CompiledParameter> getParameters() {
        return null;
    }

    // def fun(x, ...y, z = 5) { println x; println y; println z }
    @Override
    public Obj invoke(Tuple arguments) { // todo convert to tuple/obj
        Tuple tuple;
        if (!(arguments.get(0) instanceof Tuple)) {
            tuple = new Tuple(arguments.get(0));
        } else {
            tuple = (Tuple) arguments.get(0);
        }

        Scope scope = this.scope.subPool();

        boolean result = new PatternBinder(patternCase, visitor, scope).bindFrom(tuple);

        if (!result) {
            throw new InterpreterException("Could not match arguments (" + tuple + ") to " + getName() + "(" + patternCase + ")", expr.getPosition());
        }

        try {
            return expr.accept(visitor, scope);
        } catch (ReturnException re) {
            return re.getValue();
        }
    }

    @Override
    public String toString() {
        return "def " + getName() + "(" + patternCase + ")";
    }
}
